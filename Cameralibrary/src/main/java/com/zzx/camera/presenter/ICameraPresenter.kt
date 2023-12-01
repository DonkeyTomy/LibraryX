package com.zzx.camera.presenter

import android.graphics.Rect
import android.util.Size
import com.zzx.camera.view.IRecordView
import com.zzx.media.camera.ICameraManager
import com.zzx.media.recorder.video.RecorderLooper
import com.zzx.media.custom.view.opengl.renderer.SharedRender

/**@author Tomy
 * Created by Tomy on 2018/6/4.
 */
interface ICameraPresenter<surface, camera> {

    fun setRotation(rotation: Int)

    fun openSpecialCamera(id: Int = 0)

    fun openFrontCamera()

    fun openBackCamera()

    fun isCameraOpening(): Boolean

    fun getCameraCount(): Int

    fun getCameraManager(): ICameraManager<surface, camera>

    /**
     * @see [stopPreview]
     */
    fun startPreview()

    /**
     * @see [stopPreview]
     */
    fun stopPreview()

    /**前提得先调用[setPictureCallback]方法来设置图片数据回调,否则拍照后图标数据不会被回传.
     */
    fun takePicture()

    fun setSensorOrientation(orientation: Int)

    /**
     * 前提得先调用[setPictureCallback]方法来设置图片数据回调,否则拍照后图标数据不会被回传.
     * @param burstCount Int 高速连拍的照片数
     */
    fun takeBurstPicture(burstCount: Int)

    fun initCameraParams()

    fun setPreviewParams(width: Int, height: Int, format: Int)

    /**
     * 设置渲染窗口的分辨率.必须是手机屏幕上的实际像素分辨率.否则会导致只显示一部分或者部分黑屏.
     * @param width Int
     * @param height Int
     */
//    fun setSurfaceSize(width: Int, height: Int)

    fun setCaptureParams(width: Int, height: Int)

    /**
     * 设置照片的数据回调,[takePicture]/[takeBurstPicture]之前起码要设置一次,否则数据将会被回收.
     * @param callback ICameraManager.PictureDataCallback? 照片数据回调方法.
     */
    fun setPictureCallback(callback: ICameraManager.PictureCallback?)

    /**
     * 开始录像.
     * @param isLooper Boolean 是否调用循环录像.与[stopRecord]必须对应,否则不会取消已启动的循环录像.
     * @param refreshUI 是否刷新UI.可用于只后台录像不刷新UI.
     */
    fun startRecord(isLooper: Boolean = true, refreshUI: Boolean = true)

    fun getStartRecordTime(): Long

    fun getStopRecordTime(): Long

    /**
     * 检查是否启动预录功能
     */
    fun checkPreRecordEnabled(needStart: Boolean = true): Boolean

    /**
     * 检查是否启动延录功能
     */
    fun checkDelayRecordEnabled(needStart: Boolean = true): Boolean

    /**
     * 停止录像.
     * @param isLooper Boolean 是否调用循环录像.与[stopRecord]必须对应,否则不会取消已启动的录像.
     * @param enableCheckPreOrDelay 加个参数代表是否启动检查功能,某些情况下要禁止检查,如切换到录音的情况下,否则会导致双方MIC冲突.
     */
    fun stopRecord(isLooper: Boolean = true, enableCheckPreOrDelay: Boolean = true): Boolean

    /**
     * 切换录像状态: stop/start
     */
    fun toggleRecord()

    /**
     * 锁定当前录像文件,若当前没录像则会先启动录像.
     */
    fun lockRecord()

    /**
     * UI是否处于录像状态,调用[isUIRecording]
     * @return Boolean Recorder是否正在录像(真正意义上的录像操作).
     */
    fun isRecording(): Boolean

    fun isRecordStartingOrStopping(): Boolean

    fun isLoopRecording(): Boolean

    fun isCameraBusy(): Boolean

    /**
     * 要确定后台是否处于录像,调用[isRecording]
     * @return Boolean UI是否处于录像状态.也可能UI未处于录像状态,[isRecording]而后台Recorder本身是正在录像的(预录延录模式下).
     */
    fun isUIRecording(): Boolean

    /**
     * 开始自动对焦
     */
    fun startAutoFocus(focusCallback: ICameraManager.AutoFocusCallback? = null)

    /**
     * 设置自动对焦回调,若已设置,则[startAutoFocus]可以不再传入
     * @param focusCallback AutoFocusCallback?
     */
    fun setAutoFocusCallback(focusCallback: ICameraManager.AutoFocusCallback?)

    /**
     * 停止自动对焦
     */
    fun cancelAutoFocus()

    /**
     * @param focusRect 设置定点对焦的区域
     */
    fun focusOnRect(focusRect: Rect, focusCallback: ICameraManager.AutoFocusCallback? = null)

    /**
     * @param x 在Camera窗口X相对坐标
     * @param y 在Camera窗口Y相对坐标
     * @param screenWidth Camera窗口的宽度
     * @param screenHeight Camera窗口的高度
     * @param horWidth 区域相对中心的左右扩展(-horWidth, horWidth)默认为100
     * @param verHeight 区域相对中心的上下扩展.(-verHeight, verHeight)默认为100
     */
    fun focusOnPoint(x: Int, y: Int, screenWidth: Int, screenHeight: Int, horWidth: Int = 100, verHeight: Int = 100, focusCallback: ICameraManager.AutoFocusCallback? = null)

    /**
     * 释放
     */
    fun releaseCamera()

    fun release()

    fun zoomUp(level: Int = 1)

    fun zoomDown(level: Int = 1)

    fun getZoomMax(): Int

    fun setZoomLevel(level: Int)

    fun getSupportPictureSizeList(): Array<Size>

    fun setFlashOn()

    fun setFlashOff()

    fun setColorEffect(colorEffect: String)

    fun getColorEffect(): String


    fun isSurfaceCreated(): Boolean

    fun setRecordStateCallback(callback: RecorderLooper.IRecordLoopCallback?)

    fun setCameraCallback(callback: CameraStateCallback?)

    fun setPreviewCallback(callback: ICameraManager.PreviewDataCallback?)

    /**
     * 显示录像状态.8.0后悬浮窗中AnimationDrawable显示会有问题,需要放置在显示悬浮窗的时候去调用.
     * @param show Boolean
     */
    fun showRecordingStatus(show: Boolean)

    fun getRecordView(): IRecordView

    fun registerPreviewSurface(surface: Any, width: Int, height: Int, needCallback: Boolean = false, surfaceNeedRelease: Boolean = false)

    fun unregisterPreviewSurface(surface: Any)

    fun setOnFrameRenderListener(listener: SharedRender.OnFrameRenderListener?)

    interface CameraStateCallback {

        fun onCameraOpening()

        fun onCameraClosing()

        fun onCameraErrorClose(errorCode: Int)

        fun onCameraClosed()

        fun onCameraOpenSuccess(id: Int)

        fun onCameraPreviewSuccess()

        fun onCameraPreviewStop()

        fun onCameraOpenFailed(errorCode: Int)
    }

    interface RecordStateCallback {

        fun onRecordError(error: Int)

        fun onRecordFinished()
    }
}