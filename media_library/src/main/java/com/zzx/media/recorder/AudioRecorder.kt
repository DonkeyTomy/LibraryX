package com.zzx.media.recorder

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.IntDef
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableEmitter
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean

/**@author Tomy
 * Created by Tomy on 2018/1/15.
 */
@SuppressLint("MissingPermission")
class AudioRecorder(sampleRate: Int, channelCount: Int, @Format audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT,
                    private var mAudioReadCallback: IAudioRecorder.AudioReadCallback? = null) {

    private var mRecorder: AudioRecord
    private var mBufferMinSize: Int = 0
    private val mBuffer: ByteBuffer
    private var mBufferArray: ByteArray
    private var mIsInterrupt: AtomicBoolean = AtomicBoolean(false)

    init {
        val channelConfig = when(channelCount) {
            1 -> AudioFormat.CHANNEL_IN_MONO
            2 -> AudioFormat.CHANNEL_IN_STEREO
            else -> {
                AudioFormat.CHANNEL_IN_MONO
            }
        }
        mBufferMinSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        W("channelConfig[$channelConfig].mBufferMinSize = $mBufferMinSize audioFormat[$audioFormat]")
        mRecorder = AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, mBufferMinSize)
        mBuffer = ByteBuffer.allocate(mBufferMinSize)
        mBufferArray = ByteArray(mBufferMinSize)
    }

    fun setAudioReadCallback(audioReadCallback: IAudioRecorder.AudioReadCallback) {
        mAudioReadCallback = audioReadCallback
    }

    fun stopRecord() {
        mIsInterrupt.set(true)
        mRecorder.stop()
    }

    fun startRecord() {
        mIsInterrupt.set(false)
        mRecorder.startRecording()
        val pcmData = PcmData()
        var size: Int
        Flowable.create({
            e: FlowableEmitter<PcmData> ->
            run {
                while (!mIsInterrupt.get()) {
                    mBuffer.clear()
                    size = mRecorder.read(mBufferArray, 0, mBufferMinSize)
                    W("size = $size")
                    /*if (size != mBufferMinSize) {
                        mBufferArray = ByteArray(size)
                    }*/
                    if (size > 0) {
                        pcmData.data = mBufferArray.copyOf(size)
                        pcmData.size = size
                        e.onNext(pcmData)
                    }
                }
                e.onComplete()
            }
        }, BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .subscribe {
                    data: PcmData ->
                    mAudioReadCallback?.onAudioRead(data.data!!, data.size)
                }
    }

    fun W(msg: String) {
        Timber.d("<-------------- $msg ----------->")
    }

    fun E(msg: String) {
        Timber.e("<-------------- $msg ----------->")
    }

    @IntDef(AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Format

    data class PcmData(
        var data: ByteArray? = null,
        var size: Int = 0
    )

}

