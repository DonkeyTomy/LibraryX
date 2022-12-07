package com.zzx.camera.h9.addition

import java.io.File

/**@author Tomy
 * Created by Tomy on 2018/10/22.
 */
interface ICaptureAddition {

    /**
     * 拍照的用户入口
     */
    fun takePicture()

    /**
     * 定时间隔拍照
     */
    fun takeIntervalPicture()

    /**
     * 延迟拍照
     */
    fun takeDelayPicture()

    /**
     * 清除一切拍照模式
     */
    fun clearPictureMode(needRefreshUI: Boolean = true)

    /**
     * 高速连拍
     */
    fun takeBurstPicture()

    fun isIntervalOrDelayMode(): Boolean

    fun isCapturing(): Boolean

    fun isUserCapturing(): Boolean

    interface ICaptureCallback {

        fun onCaptureDone(file: File?)

        fun onCaptureFinish()

        fun onCaptureFailed(errorCode: Int = -1)
    }

}