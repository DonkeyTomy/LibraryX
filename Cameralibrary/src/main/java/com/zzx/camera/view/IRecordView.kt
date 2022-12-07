package com.zzx.camera.view

import java.io.File

/**@author Tomy
 * Created by Tomy on 2018/10/3.
 */
abstract class IRecordView {

    /** 是否正在录像 **/
    protected var mIsRecording = false

    protected var mStartTime    = 0L
    protected var mStopTime     = 0L

    abstract fun startRecord(needTTS: Boolean = true)

    abstract fun stopRecord(needTTS: Boolean = true)

    fun isRecording() = mIsRecording

    fun getStartTime() = mStartTime

    fun getStopTime() = mStopTime

    abstract fun isShow(show: Boolean)

    /**
     * 为了解决几率性出现动画图标变成一个点.
     * 只有在显示录像窗口以及正在录像的情况下才会显示录像动画.
     * */
    abstract fun showRecordStatus()

    open fun showImpIcon(show: Boolean) {}

    open fun focusOnPoint(x: Float, y: Float) {}

    open fun focusSuccess(success: Boolean) {}

    /**
     * 提示正在录像.无法操作.
     */
    open fun noticeRecording(recording: Boolean) {}

    /**
     * 显示缩略图
     * @param file File?
     */
    open fun showThumb(file: File?) {}

    open fun lockRecordStarting(needTTS: Boolean = true) {}

    open fun lockRecordFinished(success: Boolean, needTTS: Boolean = true) {}

    /***
     * 开始拍照.屏蔽一些操控相机的操作.如屏蔽拍照、录像按钮.
     */
    abstract fun takePictureStart()

    /***
     * 拍照结束.恢复一些操控相机的操作.
     */
    abstract fun takePictureFinish()

    /**
     * 手动拍照开始.屏蔽相机设置操作.如设置参数，切换相机等。
     */
    abstract fun manualCaptureStart()

    /**
     * 手动拍照结束.屏蔽相机设置操作.如设置参数，切换相机等。
     */
    abstract fun manualCaptureFinish()

    abstract fun isManualCapturing(): Boolean

    abstract fun recordError(msgId: Int)

    abstract fun recordError(errorMsg: String)

    abstract fun showMsg(msgId: Int)

    abstract fun disableCameraBtn()
    abstract fun enableCameraBtn()
}