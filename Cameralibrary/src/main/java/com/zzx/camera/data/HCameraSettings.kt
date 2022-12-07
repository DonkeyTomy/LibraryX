package com.zzx.camera.data

import android.content.Context
import android.preference.PreferenceManager
import android.provider.Settings
import com.zzx.camera.R
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2018/10/4.
 */
class HCameraSettings(context: Context): RecordSettings(context, PreferenceManager.getDefaultSharedPreferencesName(context)) {


    fun setCameraMode(mode: Int) {
        mPreferences.saveInt(CAMERA_MODE, mode)
    }
    fun getCameraMode() = mPreferences.getInt(CAMERA_MODE, DEFAULT_CAMERA_MODE_VIDEO)


    override fun setRecordDuration(duration: Int) {
        mPreferences.saveInt(RECORD_DURATION, duration * 60)
    }
    override fun getRecordDuration() = mPreferences.getString(RECORD_DURATION, DEFAULT_DURATION).toInt() * 60 + 2


    fun setRecordAuto(auto: Boolean) {
        mPreferences.saveBoolean(RECORD_AUTO, auto)
    }
    fun getRecordAuto() = mPreferences.getString(RECORD_AUTO, DEFAULT_AUTO_CLOSE) != DEFAULT_AUTO_CLOSE


    fun setRecordPre(pre: Int) {
        mPreferences.saveString(RECORD_PRE, pre.toString())
    }
    fun getRecordPre() = mPreferences.getString(RECORD_PRE, DEFAULT_PRE).toInt()


    fun setRecordDelay(delay: Int) {
        mPreferences.saveString(RECORD_DELAY, delay.toString())
    }

    fun getRecordDelay() = mPreferences.getString(RECORD_DELAY, DEFAULT_RECORD_DELAY).toInt()


    fun setVideoRatio(ratio: Int) {
        mPreferences.saveInt(RECORD_RATIO, ratio)
    }
    fun getVideoRatio() = mPreferences.getInt(RECORD_RATIO, DEFAULT_RECORD_RATIO)

    fun setVideoRatioBack(ratio: Int) {
        mPreferences.saveInt(RECORD_RATIO_BACK, ratio)
    }
    fun getVideoRatioBack() = mPreferences.getInt(RECORD_RATIO_BACK, DEFAULT_RECORD_RATIO)

    fun setVideoRatioFront(ratio: Int) {
        mPreferences.saveInt(RECORD_RATIO_FORNT, ratio)
    }
    fun getVideoRatioFront() = mPreferences.getInt(RECORD_RATIO_FORNT, DEFAULT_RECORD_RATIO_FRONT)


    fun setPhotoMode(mode: Int) {
        mPreferences.saveString(PHOTO_MODE, mode.toString())
    }
    fun getPhotoMode() = mPreferences.getString(PHOTO_MODE, DEFAULT_PHOTO_MODE).toInt()

    fun getPhotoModeKey(): String {
        val mode = getPhotoMode()
        Timber.e("$TAG_C_S photoMode = $mode")
        val keyId = when (mode) {
            PhotoMode.CONTINUOUS.ordinal    -> R.string.key_mode_continuous
            PhotoMode.INTERVAL.ordinal  -> R.string.key_mode_interval
            PhotoMode.TIMER.ordinal     -> R.string.key_mode_timer
            else -> R.string.key_mode_picture
        }
        return getKey(keyId)
    }

    fun getRecordHighQuality() = mPreferences.getString(RECORD_QUALITY, DEFAULT_RECORD_QUALITY_HIGH).toInt() == 1

    fun getNeedLoop(): Boolean {
        return Settings.System.getInt(mContext.contentResolver, RECORD_LOOP, -1) == 1
    }

    fun setNeedLoop(needLoop: Boolean) {
        Settings.System.putInt(mContext.contentResolver, RECORD_LOOP, if (needLoop) 1 else 0)
    }


    fun setPhotoRatio(ratio: Int) {
        mPreferences.saveInt(PHOTO_RATIO, ratio)
    }
    fun getPhotoRatio() = mPreferences.getInt(PHOTO_RATIO, DEFAULT_PHOTO_RATIO)


    fun setPhotoContinuousCount(count: Int) {
        mPreferences.saveInt(PHOTO_CONTINUOUS_COUNT, count)
    }
    fun getPhotoContinuousCount() = mPreferences.getString(PHOTO_CONTINUOUS_COUNT, DEFAULT_CONTINUOUS_COUNT).toInt()


    fun setPhotoIntervalCount(count: Int) {
        mPreferences.saveInt(PHOTO_INTERVAL_COUNT, count)
    }
    fun getPhotoIntervalCount() = mPreferences.getString(PHOTO_INTERVAL_COUNT, DEFAULT_INTERVAL_COUNT).toInt()


    fun setPhotoTimerCount(count: Int) {
        mPreferences.saveInt(PHOTO_TIMER_COUNT, count)
    }
    fun getPhotoTimerCount() = mPreferences.getString(PHOTO_TIMER_COUNT, DEFAULT_TIMER_COUNT).toInt()
    
    fun getKey(keyId: Int): String {
        return mContext.getString(keyId)
    }

    /** 模式: 录像/拍照 **/
    val CAMERA_MODE       = getKey(R.string.key_mode_camera)

    /** 分段录像时长 **/
    val RECORD_DURATION   = getKey(R.string.key_record_section)
    /** 录像录音 **/
    val RECORD_VOICE      = "RecordMute"
    /** 开机录像 **/
    val RECORD_AUTO       = getKey(R.string.key_record_auto)
    /** 预录 **/
    val RECORD_PRE        = getKey(R.string.key_record_pre)
    /** 延录 **/
    val RECORD_DELAY      = getKey(R.string.key_record_delay)
    /** 录像分辨率 **/
    val RECORD_RATIO      = getKey(R.string.key_ratio_record)

    val RECORD_RATIO_FORNT  = getKey(R.string.key_ratio_record_front)
    val RECORD_RATIO_BACK  = getKey(R.string.key_ratio_record_back)

    /** 拍照模式: 单拍、高速连拍、间隔连拍、定时拍照 **/
    val PHOTO_MODE    = getKey(R.string.key_mode_picture)
    val RECORD_QUALITY    = getKey(R.string.key_record_quality)
    /** 拍照分辨率 **/
    val PHOTO_RATIO   = getKey(R.string.key_ratio_picture)
    /** 高速连拍张数 **/
    val PHOTO_CONTINUOUS_COUNT    = getKey(R.string.key_mode_continuous)
    /** 间隔连拍计时 **/
    val PHOTO_INTERVAL_COUNT = getKey(R.string.key_mode_interval)
    /** 定时拍照计时 **/
    val PHOTO_TIMER_COUNT = getKey(R.string.key_mode_timer)

    /**
     * 代表拍照的模式.
     */
    enum class PhotoMode {
        SINGLE,
        CONTINUOUS,
        INTERVAL,
        TIMER
    }

    companion object {
        const val TAG_C_S = "CameraSetting:"

        const val DEFAULT_CAMERA_MODE_VIDEO = 1

        const val DEFAULT_RECORD_RATIO = 1

        const val DEFAULT_RECORD_RATIO_FRONT    = 0

        const val DEFAULT_DURATION  = "10"

        const val DEFAULT_AUTO_CLOSE  = "0"

        const val DEFAULT_PRE   = "0"

        const val DEFAULT_RECORD_DELAY    = "0"


        const val DEFAULT_PHOTO_MODE    = "0"

        const val DEFAULT_RECORD_QUALITY_HIGH    = "0"

        const val DEFAULT_PHOTO_RATIO  = 0

        const val DEFAULT_CONTINUOUS_COUNT  = "10"

        const val DEFAULT_INTERVAL_COUNT   = "5"

        const val DEFAULT_TIMER_COUNT   = "5"

        const val RECORD_VOICE_DEFAULT  = false

        const val RECORD_LOOP = "zzx_record_loop"
    }



}