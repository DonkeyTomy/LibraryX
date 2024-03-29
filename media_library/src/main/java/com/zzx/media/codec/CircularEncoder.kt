/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zzx.media.codec

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.Surface
import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference
import java.nio.ByteBuffer

/**
 * Encodes video in a fixed-size circular buffer.
 *
 *
 * The obvious way to do this would be to store each packet in its own buffer and hook it
 * into a linked list.  The trouble with this approach is that it requires constant
 * allocation, which means we'll be driving the GC to distraction as the frame rate and
 * bit rate increase.  Instead we create fixed-size pools for video data and metadata,
 * which requires a bit more work for us but avoids allocations in the steady state.
 *
 *
 * Video must always start with a sync frame (a/k/a key frame, a/k/a I-frame).  When the
 * circular buffer wraps around, we either need to delete all of the data between the frame at
 * the head of the list and the next sync frame, or have the file save function know that
 * it needs to scan forward for a sync frame before it can start saving data.
 *
 *
 * When we're told to save a snapshot, we create a MediaMuxer, write all the frames out,
 * and then go back to what we were doing.
 */
class CircularEncoder(width: Int, height: Int, bitRate: Int, frameRate: Int, desiredSpanSec: Int,
                      cb: Callback?) {
    private val mEncoderThread: EncoderThread
    /**
     * Returns the encoder's input surface.
     */
    val inputSurface: Surface
    private var mEncoder: MediaCodec?

    private var mMuxerWrapper: MuxerWrapper? = null

    /**
     * Callback function definitions.  CircularEncoder caller must provide one.
     */
    interface Callback {
        /**
         * Called some time after saveVideo(), when all data has been written to the
         * output file.
         *
         * @param status Zero means success, nonzero indicates failure.
         */
        fun fileSaveComplete(status: Int)

        /**
         * Called occasionally.
         *
         * @param totalTimeMsec Total length, in milliseconds, of buffered video.
         */
        fun bufferStatus(totalTimeMsec: Long)

        fun onEncodeReady(array: ByteArray?)

        fun onEncoderInit()
    }

    /**
     * Shuts down the encoder thread, and releases encoder resources.
     *
     *
     * Does not return until the encoder thread has stopped.
     */
    fun shutdown() {
        if (VERBOSE) Log.d(TAG, "releasing encoder objects")
        val handler: Handler? = mEncoderThread.handler
        handler!!.sendMessage(handler.obtainMessage(EncoderHandler.MSG_SHUTDOWN))
        try {
            mEncoderThread.join()
        } catch (ie: InterruptedException) {
            Log.w(TAG, "Encoder thread join() was interrupted", ie)
        }
        if (mEncoder != null) {
            mEncoder!!.stop()
            mEncoder!!.release()
            mEncoder = null
        }
    }

    fun stopRecord() {
        val handler: Handler? = mEncoderThread.handler
        handler!!.sendMessage(handler.obtainMessage(EncoderHandler.MSG_STOP))
    }

    /**
     * Notifies the encoder thread that a new frame will shortly be provided to the encoder.
     *
     *
     * There may or may not yet be data available from the encoder output.  The encoder
     * has a fair mount of latency due to processing, and it may want to accumulate a
     * few additional buffers before producing output.  We just need to drain it regularly
     * to avoid a situation where the producer gets wedged up because there's no room for
     * additional frames.
     *
     *
     * If the caller sends the frame and then notifies us, it could get wedged up.  If it
     * notifies us first and then sends the frame, we guarantee that the output buffers
     * were emptied, and it will be impossible for a single additional frame to block
     * indefinitely.
     */
    fun frameAvailableSoon() {
        val handler: Handler? = mEncoderThread.handler
        handler!!.sendMessage(handler.obtainMessage(
                EncoderHandler.MSG_FRAME_AVAILABLE_SOON))
    }

    /**
     * Initiates saving the currently-buffered frames to the specified output file.  The
     * data will be written as a .mp4 file.  The call returns immediately.  When the file
     * save completes, the callback will be notified.
     *
     *
     * The file generation is performed on the encoder thread, which means we won't be
     * draining the output buffers while this runs.  It would be wise to stop submitting
     * frames during this time.
     */
    fun saveVideo(outputFile: File?) {
        val handler: Handler? = mEncoderThread.handler
        handler!!.sendMessageDelayed(handler.obtainMessage(
                EncoderHandler.MSG_SAVE_VIDEO, outputFile), 3000.toLong())
    }

    /**
     * Object that encapsulates the encoder thread.
     *
     *
     * We want to sleep until there's work to do.  We don't actually know when a new frame
     * arrives at the encoder, because the other thread is sending frames directly to the
     * input surface.  We will see data appear at the decoder output, so we can either use
     * an infinite timeout on dequeueOutputBuffer() or wait() on an object and require the
     * calling app wake us.  It's very useful to have all of the buffer management local to
     * this thread -- avoids synchronization -- so we want to do the file muxing in here.
     * So, it's best to sleep on an object and do something appropriate when awakened.
     *
     *
     * This class does not manage the MediaCodec encoder startup/shutdown.  The encoder
     * should be fully started before the thread is created, and not shut down until this
     * thread has been joined.
     */
    class EncoderThread(private var mEncoder: MediaCodec?,
                                private val mEncBuffer: CircularEncoderBuffer,
                                private var mCallback: Callback?,
                                private var mWidth: Int = 0, private var mHeight: Int = 0) : Thread() {
        private var mEncodedFormat: MediaFormat? = null
        private val mBufferInfo: MediaCodec.BufferInfo = MediaCodec.BufferInfo()
        private var mHandler: EncoderHandler? = null
        private var mFrameNum = 0
        private val mLock = Object()
        @Volatile
        private var mReady = false

        @Volatile
        private var mRecording = false

        private val mEnableNew = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

        /**
         * Thread entry point.
         *
         *
         * Prepares the Looper, Handler, and signals anybody watching that we're ready to go.
         */
        override fun run() {
            Looper.prepare()
            mHandler = EncoderHandler(this) // must create on encoder thread
            Log.d(TAG, "encoder thread ready")
            synchronized(mLock) {
                mReady = true
                mLock.notify() // signal waitUntilReady()
            }
            Looper.loop()
            synchronized(mLock) {
                mReady = false
                mHandler = null
            }
            Log.d(TAG, "looper quit")
        }

        /**
         * Waits until the encoder thread is ready to receive messages.
         *
         *
         * Call from non-encoder thread.
         */
        fun waitUntilReady() {
            synchronized(mLock) {
                while (!mReady) {
                    try {
                        mLock.wait()
                    } catch (ie: InterruptedException) { /* not expected */
                    }
                }
                mCallback?.onEncoderInit()
            }
        }// Confirm ready state.

        /**
         * Returns the Handler used to send messages to the encoder thread.
         */
        val handler: EncoderHandler?
            get() {
                synchronized(mLock) {
                    // Confirm ready state.
                    if (!mReady) {
                        throw RuntimeException("not ready")
                    }
                }
                return mHandler
            }

        /**
         * Drains all pending output from the decoder, and adds it to the circular buffer.
         */
        fun drainEncoder() {
            val timeout = 0 // no timeout -- check for buffers, bail if none
            var encoderOutputBuffers: Array<ByteBuffer>? = null
            if (!mEnableNew) {
                encoderOutputBuffers = mEncoder!!.outputBuffers
            }
            while (true) {
                val encoderStatus = mEncoder!!.dequeueOutputBuffer(mBufferInfo, timeout.toLong())
                if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) { // no output available yet
                    break
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) { // not expected for an encoder
                    if (!mEnableNew) {
                        encoderOutputBuffers = mEncoder!!.outputBuffers
                    }
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    // Should happen before receiving buffers, and should only happen once.
                    // The MediaFormat contains the csd-0 and csd-1 keys, which we'll need
                    // for MediaMuxer.  It's unclear what else MediaMuxer might want, so
                    // rather than extract the codec-specific data and reconstruct a new
                    // MediaFormat later, we just grab it here and keep it around.
                    mEncodedFormat = mEncoder!!.outputFormat
                    Log.d(TAG, "encoder output format changed: $mEncodedFormat")
                } else if (encoderStatus < 0) {
                    Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: " +
                            encoderStatus)
                    // let's ignore it
                } else {
                    val encodedData = if (mEnableNew) {
                        mEncoder!!.getOutputBuffer(encoderStatus)
                    } else {
                        encoderOutputBuffers!![encoderStatus]
                    }

                    if (mBufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                        // The codec config data was pulled out when we got the
                        // INFO_OUTPUT_FORMAT_CHANGED status.  The MediaMuxer won't accept
                        // a single big blob -- it wants separate csd-0/csd-1 chunks --
                        // so simply saving this off won't work.
                        if (VERBOSE) Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG")
                        mBufferInfo.size = 0
                    }
                    if (mBufferInfo.size != 0) { // adjust the ByteBuffer values to match BufferInfo (not needed?)
                        encodedData?.position(mBufferInfo.offset)
                        encodedData?.limit(mBufferInfo.offset + mBufferInfo.size)
                        mEncBuffer.add(encodedData, mBufferInfo.flags,
                                mBufferInfo.presentationTimeUs)
                        if (VERBOSE) {
                            // Log.e(TAG, "sent " + mBufferInfo.size + " bytes to muxer, ts=" + mBufferInfo.presentationTimeUs);
                        }
                    }
                    mEncoder?.releaseOutputBuffer(encoderStatus, false)
                    if (mBufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                        Log.w(TAG, "reached end of stream unexpectedly")
                        break // out of while
                    }
                }
            }
        }

        fun drainEncoderToByteArray() {
            val TIMEOUT_USEC = 0 // no timeout -- check for buffers, bail if none
            var encoderOutputBuffers = mEncoder!!.outputBuffers
            var mPpsSps = ByteArray(0)
            var h264 = ByteArray(mWidth * mHeight)
            while (true) {
                val encoderStatus = mEncoder!!.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC.toLong())
                if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) { // no output available yet
                    break
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) { // not expected for an encoder
                    encoderOutputBuffers = mEncoder!!.outputBuffers
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    // Should happen before receiving buffers, and should only happen once.
                    // The MediaFormat contains the csd-0 and csd-1 keys, which we'll need
                    // for MediaMuxer.  It's unclear what else MediaMuxer might want, so
                    // rather than extract the codec-specific data and reconstruct a new
                    // MediaFormat later, we just grab it here and keep it around.
                    mEncodedFormat = mEncoder!!.outputFormat
                    Log.d(TAG, "encoder output format changed: $mEncodedFormat")
                } else if (encoderStatus < 0) {
                    Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: " +
                            encoderStatus)
                    // let's ignore it
                } else {
                    val encodedData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mEncoder!!.getOutputBuffer(encoderStatus)
                    } else {
                        encoderOutputBuffers[encoderStatus]
                    }

                    encodedData!!.position(mBufferInfo.offset)
                    encodedData.limit(mBufferInfo.offset + mBufferInfo.size)

                    var sync = false
                    if (mBufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) { // sps
                        sync = mBufferInfo.flags and MediaCodec.BUFFER_FLAG_SYNC_FRAME != 0
                        if (!sync) {
                            val temp = ByteArray(mBufferInfo.size)
                            encodedData.get(temp)
                            mPpsSps = temp
                            mEncoder?.releaseOutputBuffer(encoderStatus, false)
                            continue
                        } else {
                            mPpsSps = ByteArray(0)
                        }
                    }
                    sync = sync || (mBufferInfo.flags and MediaCodec.BUFFER_FLAG_SYNC_FRAME) != 0
                    val len = mPpsSps.size + mBufferInfo.size
                    if (len > h264.size) {
                        h264 = ByteArray(len)
                    }
                    if (sync) {
                        System.arraycopy(mPpsSps, 0, h264, 0, mPpsSps.size)
                        encodedData.get(h264, mPpsSps.size, mBufferInfo.size)
                        //mPusher.push(h264, 0, mPpsSps.length + bufferInfo.size, bufferInfo.presentationTimeUs / 1000, 1);
                        mCallback?.onEncodeReady(h264)
                        //if (BuildConfig.DEBUG)
                    } else {
                        encodedData.get(h264, 0, mBufferInfo.size)
                        mCallback?.onEncodeReady(h264)
                    }
                    mEncoder?.releaseOutputBuffer(encoderStatus, false)
                    if (mBufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                        Log.w(TAG, "reached end of stream unexpectedly")
                        break // out of while
                    }
                }
            }
        }

        /**
         * Drains the encoder output.
         *
         *
         * See notes for [CircularEncoder.frameAvailableSoon].
         */
        fun frameAvailableSoon() {
            if (VERBOSE) Log.d(TAG, "frameAvailableSoon")
            drainEncoder()
            mFrameNum++
            /*if ((mFrameNum % 10) == 0) {        // TODO: should base off frame rate or clock?
                mCallback.bufferStatus(mEncBuffer.computeTimeSpanUsec());
            }*/
        }

        fun stopRecord() {
            mRecording = false
        }

        fun startRecord() {

        }

        /**
         * Saves the encoder output to a .mp4 file.
         *
         *
         * We'll drain the encoder to get any lingering data, but we're not going to shut
         * the encoder down or use other tricks to try to "flush" the encoder.  This may
         * mean we miss the last couple of submitted frames if they're still working their
         * way through.
         *
         *
         * We may want to reset the buffer after this -- if they hit "capture" again right
         * away they'll end up saving video with a gap where we paused to write the file.
         */
        fun saveVideo(outputFile: File) {
            if (mRecording) {
                return
            }
            mRecording = true
            if (VERBOSE) Log.e(TAG, "saveVideo $outputFile")
            var index = mEncBuffer.firstIndex
            if (index < 0) {
                Log.e(TAG, "Unable to get first index")
                mCallback?.fileSaveComplete(1)
                return
            }
            val info = MediaCodec.BufferInfo()
            var muxer: MediaMuxer? = null
            var result: Int
            try {
                muxer = MediaMuxer(outputFile.path,
                        MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
                val videoTrack = muxer.addTrack(mEncodedFormat!!)
                muxer.start()
                do {
                    val buf = mEncBuffer.getChunk(index, info)
                    if (VERBOSE) {
                        Log.e(TAG, "SAVE " + index + " flags=0x" + Integer.toHexString(info.flags))
                    }

                    muxer.writeSampleData(videoTrack, buf, info)
                    index = mEncBuffer.getNextIndex(index)
                } while (mRecording && index >= 0)
                result = 0
            } catch (ioe: IOException) {
                Log.w(TAG, "muxer failed", ioe)
                result = 2
            } finally {
                if (muxer != null) {
                    muxer.stop()
                    muxer.release()
                }
            }
            if (VERBOSE) {
                Log.e(TAG, "muxer stopped, result=$result")
            }
            mCallback?.fileSaveComplete(result)
        }

        /**
         * Tells the Looper to quit.
         */
        fun shutdown() {
            if (VERBOSE) Log.d(TAG, "shutdown")
            Looper.myLooper()?.quit()
        }



    }

    companion object {
        const val TAG = "CircularEncoder"
        private const val VERBOSE = true
        private const val MIME_TYPE = "video/avc" // H.264 Advanced Video Coding
        private const val SYNC_FRAME_INTERVAL = 1 // sync frame every second

        /**
         * Handler for EncoderThread.  Used for messages sent from the UI thread (or whatever
         * is driving the encoder) to the encoder thread.
         *
         *
         * The object is created on the encoder thread.
         */
        class EncoderHandler(et: EncoderThread) : Handler() {
            // This shouldn't need to be a weak ref, since we'll go away when the Looper quits,
// but no real harm in it.
            private val mWeakEncoderThread: WeakReference<EncoderThread> = WeakReference(et)

            // runs on encoder thread
            override fun handleMessage(msg: Message) {
                val what = msg.what
                if (VERBOSE) {
                    Log.v(TAG, "EncoderHandler: what=$what")
                }
                val encoderThread = mWeakEncoderThread.get()
                if (encoderThread == null) {
                    Log.w(TAG, "EncoderHandler.handleMessage: weak ref is null")
                    return
                }
                when (what) {
                    MSG_FRAME_AVAILABLE_SOON -> encoderThread.frameAvailableSoon()
                    MSG_SAVE_VIDEO -> encoderThread.saveVideo(msg.obj as File)
                    MSG_SHUTDOWN -> encoderThread.shutdown()
                    MSG_STOP -> encoderThread.stopRecord()
                    else -> throw RuntimeException("unknown message $what")
                }
            }

            companion object {
                const val MSG_FRAME_AVAILABLE_SOON = 1
                const val MSG_SAVE_VIDEO = 2
                const val MSG_SHUTDOWN = 3
                const val MSG_STOP = 4
            }

        }
    }

    /**
     * Configures encoder, and prepares the input Surface.
     *
     * @param width Width of encoded video, in pixels.  Should be a multiple of 16.
     * @param height Height of encoded video, in pixels.  Usually a multiple of 16 (1080 is ok).
     * @param bitRate Target bit rate, in bits.
     * @param frameRate Expected frame rate.
     * @param desiredSpanSec How many seconds of video we want to have in our buffer at any time.
     */
    init {
        // The goal is to size the buffer so that we can accumulate N seconds worth of video,
        // where N is passed in as "desiredSpanSec".  If the codec generates data at roughly
        // the requested bit rate, we can compute it as time * bitRate / bitsPerByte.
        //
        // Sync frames will appear every (frameRate * IFRAME_INTERVAL) frames.  If the frame
        // rate is higher or lower than expected, various calculations may not work out right.
        //
        // Since we have to start muxing from a sync frame, we want to ensure that there's
        // room for at least one full GOP in the buffer, preferrably two.
        if (desiredSpanSec < SYNC_FRAME_INTERVAL * 2) {
            throw RuntimeException("Requested time span is too short: " + desiredSpanSec +
                    " vs. " + SYNC_FRAME_INTERVAL * 2)
        }
        val encBuffer = CircularEncoderBuffer(bitRate, frameRate,
                desiredSpanSec)
        val format = MediaFormat.createVideoFormat(MIME_TYPE, width, height)
        // Set some properties.  Failing to specify some of these can cause the MediaCodec
        // configure() call to throw an unhelpful exception.
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, SYNC_FRAME_INTERVAL)
        if (VERBOSE) Log.d(TAG, "format: $format")
        // Create a MediaCodec encoder, and configure it with our format.  Get a Surface
        // we can use for input and wrap it with a class that handles the EGL work.
        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE).apply {
            configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            inputSurface = createInputSurface()
            start()
        }

        // Start the encoder thread last.  That way we're sure it can see all of the state
        // we've initialized.
        mEncoderThread = EncoderThread(mEncoder, encBuffer, cb, width, height)
        mEncoderThread.start()
        mEncoderThread.waitUntilReady()
    }
}