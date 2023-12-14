package com.zzx.camera.data

import android.content.Context
import android.os.Build
import com.zzx.camera.R
import com.zzx.media.camera.ICameraManager
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2018/10/4.
 */
class HCameraSettings(context: Context, name: String = context.packageName, mode: Int = Context.MODE_PRIVATE): RecordSettings(context, name, mode) {


    fun setCameraMode(mode: Int) {
        mDataSaver.saveInt(CAMERA_MODE, mode)
    }
    fun getCameraMode() = mDataSaver.getInt(CAMERA_MODE, DEFAULT_CAMERA_MODE_VIDEO)


    override fun setRecordDuration(duration: Int) {
        mDataSaver.saveInt(RECORD_DURATION, duration)
    }
    override fun getRecordDuration() = mDataSaver.getInt(RECORD_DURATION, DEFAULT_DURATION)


    fun setRecordAuto(auto: Boolean) {
        mDataSaver.saveBoolean(RECORD_AUTO, auto)
    }
    fun getRecordAuto() = mDataSaver.getBoolean(RECORD_AUTO, DEFAULT_AUTO_CLOSE)

    fun setAutoInfrared(auto: Boolean) {
        mDataSaver.saveBoolean(AUTO_INFRARED, auto)
    }
    fun getAutoInfrared() = mDataSaver.getBoolean(AUTO_INFRARED, DEFAULT_AUTO_INFRARED)


    fun setRecordPre(pre: Int) {
        mDataSaver.saveInt(RECORD_PRE, pre)
    }
    fun getRecordPre() = mDataSaver.getInt(RECORD_PRE, DEFAULT_PRE)


    fun setRecordDelay(delay: Int) {
        mDataSaver.saveInt(RECORD_DELAY, delay)
    }

    fun getRecordDelay() = mDataSaver.getInt(RECORD_DELAY, DEFAULT_RECORD_DELAY)


    fun setVideoRatio(ratio: Int) {
        mDataSaver.saveInt(RECORD_RATIO, ratio)
    }
    fun getVideoRatio() = mDataSaver.getInt(RECORD_RATIO, if (isFg()) RECORD_RATIO_480 else DEFAULT_RECORD_RATIO)

    fun setVideoRatioBack(ratio: Int) {
        mDataSaver.saveInt(RECORD_RATIO_BACK, ratio)
    }
    fun getVideoRatioBack() = mDataSaver.getInt(RECORD_RATIO_BACK, DEFAULT_RECORD_RATIO)

    fun setVideoRatioFront(ratio: Int) {
        mDataSaver.saveInt(RECORD_RATIO_FORNT, ratio)
    }
    fun getVideoRatioFront() = mDataSaver.getInt(RECORD_RATIO_FORNT, DEFAULT_RECORD_RATIO_FRONT)

    fun setPreviewRatio(ratio: String) {
        mDataSaver.saveString(PREVIEW_RATIO, ratio)
    }

    fun getPreviewRatio() = mDataSaver.getString(PREVIEW_RATIO, DEFAULT_PREVIEW_RATIO)

    fun setPhotoMode(mode: Int) {
        mDataSaver.saveInt(PHOTO_MODE, mode)
    }
    fun getPhotoMode() = mDataSaver.getInt(PHOTO_MODE, DEFAULT_PHOTO_MODE)

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

    fun getRecordHighQuality() = mDataSaver.getInt(RECORD_QUALITY, DEFAULT_RECORD_QUALITY_HIGH) == 1

    fun getNeedLoop(): Boolean {
        return mDataSaver.getBoolean(RECORD_LOOP, false)
    }

    fun setNeedLoop(needLoop: Boolean) {
        mDataSaver.saveBoolean(RECORD_LOOP, needLoop)
    }

    fun isUseHevc(): Boolean {
        return mDataSaver.getBoolean(RECORD_HEVC, false)
    }

    fun setUseHevc(useHevc: Boolean) {
        mDataSaver.saveBoolean(RECORD_HEVC, useHevc)
    }


    fun setPhotoRatio(ratio: Int) {
        mDataSaver.saveInt(PHOTO_RATIO, ratio)
    }
    fun getPhotoRatio() = mDataSaver.getInt(PHOTO_RATIO, DEFAULT_PHOTO_RATIO)


    fun setPhotoContinuousCount(count: Int) {
        mDataSaver.saveInt(PHOTO_CONTINUOUS_COUNT, count)
    }
    fun getPhotoContinuousCount() = mDataSaver.getInt(PHOTO_CONTINUOUS_COUNT, 0)


    fun setPhotoIntervalCount(count: Int) {
        mDataSaver.saveInt(PHOTO_INTERVAL_COUNT, count)
    }
    fun getPhotoIntervalCount() = mDataSaver.getInt(PHOTO_INTERVAL_COUNT, DEFAULT_INTERVAL_COUNT)


    fun setPhotoTimerCount(count: Int) {
        mDataSaver.saveInt(PHOTO_TIMER_COUNT, count)
    }
    fun getPhotoTimerCount() = mDataSaver.getInt(PHOTO_TIMER_COUNT, DEFAULT_TIMER_COUNT)

    fun setStorageDir(index: Int) {
        mDataSaver.saveInt(STORAGE_DIR, index)
    }

    fun getStorageDir() = mDataSaver.getInt(STORAGE_DIR, STORAGE_EXTERNAL)

    fun isUseExternalStorage() = getStorageDir() == STORAGE_EXTERNAL

    fun getKey(keyId: Int): String {
        return mContext.getString(keyId)
    }

    fun getCameraRotationBack(): Int {
        return mDataSaver.getInt(CAMERA_ROTATION_BACK, if (Build.MODEL.contains(Regex("VTU-A|JY-G3|PSSR-A"))) {
            0
        } else {
            ICameraManager.SENSOR_BACK_CAMERA
        })
    }

    fun setCameraRotationBack(rotation: Int) {
        mDataSaver.saveInt(CAMERA_ROTATION_BACK, rotation)
    }

    /** 模式: 录像/拍照 **/
    val CAMERA_MODE       = getKey(R.string.key_mode_camera)

    /** 分段录像时长 **/
    val RECORD_DURATION   = getKey(R.string.key_record_section)
    /** 录像录音 **/
    val RECORD_VOICE      = "RecordMute"
    /** 开机录像 **/
    val RECORD_AUTO       = getKey(R.string.key_record_auto)
    val AUTO_INFRARED       = "auto_infrared"
    /** 预录 **/
    val RECORD_PRE        = getKey(R.string.key_record_pre)
    /** 延录 **/
    val RECORD_DELAY      = getKey(R.string.key_record_delay)
    /** 录像分辨率 **/
    val RECORD_RATIO      = getKey(R.string.key_ratio_record)

    val RECORD_RATIO_FORNT  = getKey(R.string.key_ratio_record_front)

    /** 预览窗口宽高比 **/
    val PREVIEW_RATIO  = getKey(R.string.key_preview_ratio)

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

    val RECORD_HEVC = getKey(R.string.key_record_hevc)

    val STORAGE_DIR = getKey(R.string.key_storage_dir)

    val CAMERA_ROTATION_BACK    = getKey(R.string.key_rotation_camera_back)

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

        fun isFg() = Build.MODEL.contains(Regex("VTU-A|JY-G3"))

        const val DEFAULT_CAMERA_MODE_VIDEO = 1

        const val RECORD_RATIO_480  = 3

        const val RECORD_RATIO_720  = 2

        const val RECORD_RATIO_1080 = 1

        const val RECORD_RATIO_HIGH = 0

        const val DEFAULT_RECORD_RATIO = RECORD_RATIO_1080

        const val DEFAULT_RECORD_RATIO_FRONT    = 0

        val DEFAULT_PREVIEW_RATIO = if (Build.MODEL.contains(Regex("680|PSSR-A|Bengal"))) "16:10" else "4:3"

        const val DEFAULT_DURATION  = 10

        const val DEFAULT_AUTO_CLOSE  = false

        const val DEFAULT_AUTO_INFRARED  = false

        const val DEFAULT_PRE   = 0

        const val DEFAULT_RECORD_DELAY    = 0


        const val DEFAULT_PHOTO_MODE    = 0

        const val DEFAULT_RECORD_QUALITY_HIGH    = 0

        val DEFAULT_PHOTO_RATIO  = if (isFg()) 3 else 2

        const val DEFAULT_CONTINUOUS_COUNT  = 10

        const val DEFAULT_INTERVAL_COUNT   = 5

        const val DEFAULT_TIMER_COUNT   = 5

        const val RECORD_VOICE_DEFAULT  = false

        const val RECORD_LOOP = "zzx_record_loop"

        const val STORAGE_INNER     = 0

        const val STORAGE_EXTERNAL  = 1
    }



}