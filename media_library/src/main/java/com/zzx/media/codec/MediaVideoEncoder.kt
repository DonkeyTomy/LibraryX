package com.zzx.media.codec

import android.annotation.SuppressLint
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.media.MediaFormat
import android.opengl.EGLContext
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Surface
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/*
 * AudioVideoRecordingSample
 * Sample project to cature audio and video from internal mic/camera and save as MPEG4 file.
 *
 * Copyright (c) 2014-2015 saki t_saki@serenegiant.com
 *
 * File name: MediaVideoEncoder.java
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * All files in the folder are under this Apache License, Version 2.0.
*/
open class MediaVideoEncoder(
    muxer: MediaMuxerWrapper?,
    listener: MediaEncoderListener?,
    val mWidth: Int,
    val mHeight: Int,
    dataListener: EncodedDataListener?,
    val mFrameRate: Int = FRAME_RATE,
    val mBitRate: Int = 0,
    val mIsUseH265: Boolean = false
) : MediaEncoder(muxer, listener, dataListener) {

    @JvmField
	var mSurface: Surface? = null

    private var mColorFormat = 0

    @SuppressLint("SuspiciousIndentation")
    @Throws(IOException::class)
    override fun prepare() {
        if (DEBUG) {
            Log.i(TAG, "prepare: ")
        }
        mTrackIndex = -1
        mIsEOS = false
        mMuxerStarted = mIsEOS
        val mimeType = if (mIsUseH265) MediaFormat.MIMETYPE_VIDEO_HEVC else MediaFormat.MIMETYPE_VIDEO_AVC
        val videoCodecInfo = selectVideoCodec(mimeType)
        if (videoCodecInfo == null) {
            Timber.tag(TAG).e("Unable to find an appropriate codec for %s", mimeType)
            return
        }
        if (DEBUG) {
            Log.i(TAG, "selected codec: " + videoCodecInfo.name)
        }
        Timber.d("[$mimeType]: size = ${mWidth}x$mHeight; frameRate = $mFrameRate; bitRate = $mBitRate")
        val format = MediaFormat.createVideoFormat(mimeType, mWidth, mHeight)
        format.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            if (mColorFormat > 0) mColorFormat else MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        val bitRateTemp = if (mBitRate == 0) calcBitRate() else mBitRate
        val bitRate = if (mIsUseH265) bitRateTemp / 2 else bitRateTemp
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, if (mFrameRate == 0) FRAME_RATE else mFrameRate)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 2)
        format.setInteger(MediaFormat.KEY_BITRATE_MODE, 2)
        Timber.d("colorFormat = $mColorFormat; bitRate = ")
        mMediaCodec = MediaCodec.createEncoderByType(mimeType)
        mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        // get Surface for encoder input
        // this method only can call between #configure and #start
        mSurface = mMediaCodec.createInputSurface() // API >= 18
        mMediaCodec.start()
        if (DEBUG) {
            Log.i(TAG, "prepare finishing")
        }
        val params = Bundle()
        params.putInt(MediaCodec.PARAMETER_KEY_REQUEST_SYNC_FRAME, 0)
        mMediaCodec.setParameters(params)
        if (mListener != null) {
            try {
                mListener.onPrepared(this)
            } catch (e: Exception) {
                Log.e(TAG, "prepare:", e)
            }
        }
    }



    fun setEglContext(shared_context: EGLContext?, tex_id: Int) {}
    override fun drain() {
        if (mMediaCodec == null) return
        var encoderOutputBuffers = mMediaCodec.outputBuffers
        var encoderStatus: Int
        val muxer = mWeakMuxer.get()
        var ppsSps = ByteArray(0)
        var h264 = ByteArray(mWidth*mHeight)
        if (muxer == null) {
//        	throw new NullPointerException("muxer is unexpectedly null");
            Log.w(TAG, "muxer is unexpectedly null")
        }
        while (mIsCapturing) {
            // get encoded data with maximum timeout duration of TIMEOUT_USEC(=10[msec])
//            Timber.v("dequeueOutput")
            encoderStatus = mMediaCodec.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC.toLong())
            when {
                encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER -> {
                    if (!mIsCapturing) {
                        break
                    } else {
//                        Timber.v("no output available, spinning to await EOS")
                    }
                }

                encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> {
                    encoderOutputBuffers = mMediaCodec.outputBuffers
                }

                encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                    if (mMuxerStarted) {
                        throw IllegalStateException("format changed twice")
                    }

                    val newFormat = mMediaCodec.outputFormat
                    Timber.d("encoder output format changed $newFormat")
                    muxer?.apply {
                        mTrackIndex = muxer.addTrack(newFormat)
                        if (!muxer.start()) {
                            // we should wait until muxer is ready
                            synchronized(muxer) {
                                while (!muxer.isStarted) try {
                                    (muxer as Object).wait(100)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                    mMuxerStarted = true
                }

                encoderStatus < 0 -> {
                    Timber.d("unexpected result from dequeueOutput")
                }

                else -> {
                    val encodedData = encoderOutputBuffers[encoderStatus]
                        ?: throw IllegalStateException("encoded output buffer")

                    /*
                    //需去掉单独编译视屏流正常.
                    if (
                        (mBufferInfo.flags and
                                MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0
                    ) {
                        Timber.d("ignoring buffer_flag_codec")
                        mBufferInfo.size = 0
                    }*/

                    if (mBufferInfo.size != 0) {
                        if (!mMuxerStarted) {
                            throw IllegalStateException("muxer hasn't started")
                        }

                        encodedData.position(mBufferInfo.offset)
                        encodedData.limit(
                            mBufferInfo.offset + mBufferInfo.size
                        )

                        muxer?.writeSampleData(
                            mTrackIndex, encodedData, mBufferInfo
                        )
                        if (mDataListener != null) {
                            var sync = false
                            if (mBufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                                sync = mBufferInfo.flags and MediaCodec.BUFFER_FLAG_SYNC_FRAME != 0
                                if (!sync) {
                                    val temp = ByteArray(mBufferInfo.size)
                                    encodedData.get(temp)
                                    ppsSps = temp
                                    mMediaCodec.releaseOutputBuffer(encoderStatus, false)
                                    continue
                                } else {
                                    ppsSps = ByteArray(0)
                                }
                            }
                            sync = sync or (mBufferInfo.flags.and(MediaCodec.BUFFER_FLAG_SYNC_FRAME) != 0)
                            val len = ppsSps.size + mBufferInfo.size
                            if (len > h264.size) {
                                h264 = ByteArray(len)
                            }
                            val size = if (sync) {
                                System.arraycopy(ppsSps, 0, h264, 0, ppsSps.size)
                                encodedData.get(h264, ppsSps.size, mBufferInfo.size)
//                                Timber.d("pushEncodeFrame: ppsSps")
                                ppsSps.size + mBufferInfo.size
                            } else {
                                encodedData.get(h264, 0, mBufferInfo.size)
//                                Timber.d("pushEncodeFrame: full")
                                mBufferInfo.size
                            }
                            mDataListener.onDataEncoded(h264, size)
//                            mTempFile.write(h264, 0, size)
                        }
                        Timber.v("send ${mBufferInfo.size}")
                    }

                    mMediaCodec.releaseOutputBuffer(encoderStatus, false)

                    if (
                        (mBufferInfo.flags and
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0
                    ) {
                        if (!mIsCapturing) {
                            Timber.d("reached end of stream unexpectedly")
                        } else {
                            Timber.d("end of stream reached")
                        }
                        break
                    }
                }
            }
        }
    }

    override fun release() {
        if (DEBUG) Log.i(TAG, "release:")
        /*if (mSurface != null) {
			mSurface.release();
			mSurface = null;
		}*/
        mTempFile.flush()
        mTempFile.close()
        super.release()
    }

    private fun calcBitRate(): Int {
        val bitrate = (BPP * FRAME_RATE * mWidth * mHeight).toInt()
        Log.i(TAG, String.format("bitrate=%5.2f[Mbps]", bitrate / 1024f / 1024f))
        return bitrate
    }

    /**
     * select the first codec that match a specific MIME type
     * @param mimeType
     * @return null if no codec matched
     */
    protected fun selectVideoCodec(mimeType: String): MediaCodecInfo? {
        if (DEBUG) Log.v(TAG, "selectVideoCodec:")

        // get the list of available codecs
        val numCodecs = MediaCodecList.getCodecCount()
        for (i in 0 until numCodecs) {
            val codecInfo = MediaCodecList.getCodecInfoAt(i)
            if (!codecInfo.isEncoder) {    // skipp decoder
                continue
            }
            // select first codec that match a specific MIME type and color format
            val types = codecInfo.supportedTypes
            for (j in types.indices) {
                if (types[j].equals(mimeType, ignoreCase = true)) {
                    if (DEBUG) Log.i(TAG, "codec:" + codecInfo.name + ",MIME=" + types[j])
                    val format = selectColorFormat(codecInfo, mimeType)
                    if (format > 0) {
                        mColorFormat = format
                        return codecInfo
                    }
                }
            }
        }
        return null
    }

    private val mTempFile by lazy {
        FileOutputStream(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "test1.mp4"))
    }

    init {
        mIsVideo = true
        if (DEBUG) {
            Timber.i( "MediaVideoEncoder: size = ${mWidth}x$mHeight")
        }
        if (muxer == null) {
            try {
                prepare()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    override fun signalEndOfInputStream() {
//        if (DEBUG) {
        Log.w(TAG, "signalEndOfInputStream")
//        }
        mMediaCodec.signalEndOfInputStream() // API >= 18
        mIsEOS = true
    }

    companion object {
        private const val TAG = "VideoMediaEncoderT"
//        private const val MIME_TYPE = MediaFormat.MIMETYPE_VIDEO_AVC

        // parameters for recording
        private const val FRAME_RATE = 15
        private const val BPP = 0.13f

        /**
         * select color format available on specific codec and we can use.
         * @return 0 if no colorFormat is matched
         */
        protected fun selectColorFormat(codecInfo: MediaCodecInfo, mimeType: String): Int {
            if (DEBUG) Log.i(TAG, "selectColorFormat: ")
            var result = 0
            val caps: MediaCodecInfo.CodecCapabilities
            try {
                Thread.currentThread().priority = Thread.MAX_PRIORITY
                caps = codecInfo.getCapabilitiesForType(mimeType)
            } finally {
                Thread.currentThread().priority = Thread.NORM_PRIORITY
            }
            var colorFormat: Int
            for (i in caps.colorFormats.indices) {
                colorFormat = caps.colorFormats[i]
                Timber.d("colorFormat = %s", colorFormat)
                if (isRecognizedViewFormat(colorFormat)) {
                    if (result == 0) result = colorFormat
                    break
                }
            }
            if (result == 0) {
                Log.e(TAG, "couldn't find a good color format for " + codecInfo.name + " / " + mimeType)
            }
            return result
        }

        /**
         * color formats that we can use in this class
         */
        protected var recognizedFormats: IntArray? = intArrayOf(
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar,
            MediaCodecInfo.CodecCapabilities.COLOR_QCOM_FormatYUV420SemiPlanar,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )

        private fun isRecognizedViewFormat(colorFormat: Int): Boolean {
            if (DEBUG) Log.i(TAG, "isRecognizedViewFormat:colorFormat=$colorFormat")
            val n = if (recognizedFormats != null) recognizedFormats!!.size else 0
            for (i in 0 until n) {
                if (recognizedFormats!![i] == colorFormat) {
                    return true
                }
            }
            return false
        }
    }
}