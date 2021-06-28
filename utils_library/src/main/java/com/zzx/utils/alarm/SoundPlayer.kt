package com.zzx.utils.alarm

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.provider.Settings
import timber.log.Timber


/**@author Tomy
 * Created by Tomy on 2018/10/31.
 */
class SoundPlayer private constructor() {


    @SuppressLint("UseSparseArrays")
    private val mSoundIdMap = HashMap<Int, Int>()

    private var mSoundRawId = 0

    private var mLoop = 0

    private var mRate = 1f

    private var mContext: Context? = null

    fun init(context: Context) {
        mContext = context
    }


    private val mSoundPool by lazy {
        SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setLegacyStreamType(AudioManager.STREAM_SYSTEM)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build())
                .build().apply {
                    setOnLoadCompleteListener { _, sampleId, status ->
                        if (status == 0) {
                            mSoundIdMap[mSoundRawId] = sampleId
                            playSoundId(sampleId, mLoop, mRate)
                        }
                    }
                }
    }

    fun playSound(context: Context, soundRawId: Int, loop: Int = 0, rate: Float = 1.0f) {
        /*if (!isSpeechEnabled(context)) {
            return
        }*/
        mSoundRawId = soundRawId
        mLoop = loop
        mRate = rate
        val soundId = mSoundIdMap[soundRawId]
        Timber.d("playSoundId = $soundId")
        if  (soundId == null) {
            mSoundPool.load(context, soundRawId, 1)
        } else {
            playSoundId(soundId, loop, rate)
        }
    }

    fun playSound(soundRawId: Int, loop: Int = 0, rate: Float = 1.0f) {
        /*if (!isSpeechEnabled(context)) {
            return
        }*/
        mSoundRawId = soundRawId
        mLoop = loop
        mRate = rate
        val soundId = mSoundIdMap[soundRawId]
        Timber.d("playSoundId = $soundId")
        if  (soundId == null) {
            if (mContext != null) {
                mSoundPool.load(mContext, soundRawId, 1)
            } else {
                Timber.e("Need call init() first!!!")
            }
        } else {
            playSoundId(soundId, loop, rate)
        }
    }

    private fun isSpeechEnabled(context: Context): Boolean {
        return Settings.System.getInt(context.contentResolver, SPEECH_ENABLED, 1) == 1
    }

    fun stopPlay(soundRawId: Int) {
        val soundID = mSoundIdMap[soundRawId]
        if (soundID != null) {
            mSoundPool.stop(soundID)
        }
    }


    fun startContinuousCapture() {
        mSoundPool.load("/system/media/audio/ui/camera_shutter.ogg", 1)
    }


    /**
     * @param soundID Int 播放的音源ID
     * @param loop Int 循环的次数: -1代表不断循环直至调用[SoundPool.stop]. 0代表只播放一次.其他非0正整数代表重复的次数
     * @param rate Float 播放的频率.1.0f代表正常频率.范围:0.5 ~ 2.0
     */
    private fun playSoundId(soundID: Int, loop: Int = 0, rate: Float = 1f) {
        mSoundPool.play(soundID, 1.0f, 1f, 1, loop, rate)
    }

    private fun release() {
        mSoundIdMap.clear()
        mSoundPool.release()
        mContext = null
    }

    companion object {
        const val SPEECH_ENABLED = "zzx_speech_enabled"

        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: SoundPlayer? = null

        fun getInstance(): SoundPlayer {
            if (INSTANCE == null) {
                INSTANCE = SoundPlayer()
            }
            return INSTANCE!!
        }

        fun release() {
            INSTANCE?.release()
            INSTANCE = null
        }
    }
}