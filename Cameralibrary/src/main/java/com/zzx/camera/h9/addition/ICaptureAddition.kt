package com.zzx.camera.h9.addition

import java.io.File

/**@author Tomy
 * Created by Tomy on 2018/10/22.
 */
interface ICaptureAddition {

    /**
     * 拍照的用户入口
     */
    fun takePicture(needResult: Boolean = false)

    /**
     * 定时间隔拍照
     */
    fun takeIntervalPicture(needResult: Boolean = false)

    /**
     * 延迟拍照
     */
    fun takeDelayPicture(needResult: Boolean = false)

    /**
     * @param needResult if true. see[ACTION_CAPTURE_RESULT]&[RESULT_PIC_PATH]
     */
    fun takeOneShot(needResult: Boolean = false)

    /**
     * 清除一切拍照模式
     */
    fun clearPictureMode(needRefreshUI: Boolean = true)

    /**
     * 高速连拍
     */
    fun takeBurstPicture(needResult: Boolean = false)

    fun isIntervalOrDelayMode(): Boolean

    fun isCapturing(): Boolean

    fun isUserCapturing(): Boolean

    fun release()

    interface ICaptureCallback {

        fun onCaptureDone(file: File?)

        fun onCaptureFinish()

        fun onCaptureStart()

        fun onUserCaptureStart()

        fun onCaptureError(errorCode: Int)
    }

    companion object {
        const val ACTION_CAPTURE_RESULT = "CaptureAdditionResult"
        const val RESULT_PIC_PATH   = "result"
    }

}