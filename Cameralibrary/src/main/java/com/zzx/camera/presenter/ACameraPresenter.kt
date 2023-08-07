package com.zzx.camera.presenter

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.media.MediaRecorder
import android.os.SystemClock
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import com.zzx.camera.R
import com.zzx.camera.data.Global
import com.zzx.camera.data.HCameraSettings
import com.zzx.camera.h9.controller.HViewController
import com.zzx.camera.values.CommonConst
import com.zzx.camera.view.IRecordView
import com.zzx.log.LogReceiver
import com.zzx.media.camera.ICameraManager
import com.zzx.media.camera.v1.manager.Camera1Manager
import com.zzx.media.custom.view.camera.ISurfaceView
import com.zzx.media.recorder.IRecorder
import com.zzx.media.recorder.video.RecorderLooper
import com.zzx.media.custom.view.opengl.renderer.SharedRender
import com.zzx.utils.StorageListener
import com.zzx.utils.TTSToast
import com.zzx.utils.file.IFileLocker
import com.zzx.utils.rxjava.FlowableUtil
import com.zzx.utils.zzx.SystemInfo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**@author Tomy
 * Created by Tomy on 2018/10/7.
 */
abstract class ACameraPresenter<surface, camera>(protected var mContext: Context, protected var mICameraManager: ICameraManager<surface, camera>,
                                                 var mCameraView: ISurfaceView<surface, camera>, var mRecordView: IRecordView,
                                                 var mStorageListener: StorageListener) : ICameraPresenter<surface, camera>, View.OnTouchListener,
    SharedRender.OnSurfaceTextureReadyListener {

    private var mFocusEnable = AtomicBoolean(true)

    private val mNeedDelay = if (SystemInfo.getDeviceModel().contains("k94", true)) 800L else 100L

    protected var mNeedReleaseCamera = false

    /**
     * 录像开启的系统时间[SystemClock.elapsedRealtime].用于限制录像,开始录像一秒内不能停止,否则会报错.
     */
    protected var mRecordStartTime = 0L

    protected var mRecordStopTime = 0L

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (mICameraManager.isManualFocusSupported() && event.action == MotionEvent.ACTION_DOWN) {
            if (mFocusEnable.get()) {
                mFocusEnable.set(false)
                mRecordView.focusOnPoint(event.rawX, event.rawY)
                focusOnPoint(event.x.toInt(), event.y.toInt(), mSurfaceViewWidth, mSurfaceViewHeight)
                Observable.just(Unit)
                        .delay(500, TimeUnit.MILLISECONDS)
                        .subscribe {
                            mFocusEnable.set(true)
                        }
            }

        }
        return false
    }

    private var mSurfaceViewWidth = 0
    private var mSurfaceViewHeight = 0

    protected val mSharedRender = SharedRender(mContext).apply {
        setOnSurfaceTextureReadyListener(this@ACameraPresenter)
    }

    protected var mSurface: SurfaceTexture? = null

    var mSurfaceHolder: surface? = null

    protected val mSurfaceCreated = AtomicBoolean(false)

    protected val mCameraOpened = AtomicBoolean(false)

    private val mObject = Object()

    protected var mRecorderLooper: RecorderLooper<surface, camera>? = null

    protected var mPictureCallback: ICameraManager.PictureCallback? = null

    protected var mRotation = 0

    protected var mLocking = AtomicBoolean(false)

    protected var mPreRecording = AtomicBoolean(false)

    protected val mIsCamera1 = mICameraManager is Camera1Manager

    protected var mNeedLock = false

    private var mCameraStateCallback: ICameraPresenter.CameraStateCallback? = null

    //    private var mRecordCallback: ICameraPresenter.RecordStateCallback? = null
    private var mRecordCallback: RecorderLooper.IRecordLoopCallback? = null

    protected var mNeedCheckPreMode = false


    init {
//        mCameraView.setPreviewSize(864, 480)
        mCameraView.setOnTouchListener(this)
        mCameraView.setStateCallback(SurfaceListener())

        mStorageListener.setStorageCallback(StorageCallback())
        initRecordLooper()
        FlowableUtil.setBackgroundThread {
            mICameraManager.apply {
                Timber.d("firstOpen: ${getCameraCore().getCameraID()}")
                setStateCallback(CameraStateCallback())
                setPictureCallback(PictureCallback())
                setAutoFocusCallback(FocusCallback())
                when (getCameraCore().getCameraID()) {
                    HViewController.CAMERA_ID_REC -> {
                        openBackCamera()
                    }
                    HViewController.CAMERA_ID_FRONT -> {
                        openFrontCamera()
                    }
                    else -> {
                        openExternalCamera()
                    }
                }
            }
        }
    }

    private fun initRecordLooper() {
        mRecorderLooper = RecorderLooper<surface, camera>(mContext, IRecorder.VIDEO).apply {
            setErrorAutoStart(true)
            setDirPath(CommonConst.getVideoDir(mContext)!!.absolutePath)
            setCameraManager(mICameraManager)
            setRecordCallback(RecordCallback())
        }
    }

    override fun setRotation(rotation: Int) {
        mRotation = rotation
        mRecorderLooper?.setRotation(mRotation)
    }

    override fun setRecordStateCallback(callback: RecorderLooper.IRecordLoopCallback?) {
        mRecordCallback = callback
    }

    override fun setPreviewCallback(callback: ICameraManager.PreviewDataCallback?) {
        mICameraManager.setPreviewDataCallback(callback)
    }

    /*override fun setSurfaceSize(width: Int, height: Int) {
        mSharedRender.setRenderSize(width, height)
    }*/

    override fun openSpecialCamera(id: Int) {
        mICameraManager.openExternalCamera()
    }

    override fun openFrontCamera() {
        mICameraManager.openFrontCamera()
    }

    override fun openBackCamera() {
        mICameraManager.openBackCamera()
    }

    override fun isCameraOpening(): Boolean {
        return mICameraManager.isCameraOpening()
    }

    override fun getCameraCount(): Int {
        return mICameraManager.getCameraCount()
    }

    override fun getCameraManager(): ICameraManager<surface, camera> {
        return mICameraManager
    }

    /**
     * UI是否处于录像状态,调用[isUIRecording]
     * @return Boolean Recorder是否正在录像(真正意义上的录像操作).
     */
    override fun isRecording(): Boolean {
        return mRecorderLooper?.isRecording() ?: false
    }

    override fun isCameraBusy(): Boolean {
        return mICameraManager.getCameraCore().isBusy()
    }

    override fun isRecordStartingOrStopping(): Boolean {
        return mRecorderLooper?.isRecordStartingOrStopping() ?: false
    }

    override fun isLoopRecording(): Boolean {
        return mRecorderLooper?.isLoopRecording() ?: false
    }

    /**
     * 要确定后台是否处于录像,调用[isRecording]
     * @return Boolean UI是否处于录像状态.也可能UI未处于录像状态,[isRecording]而后台Recorder本身是正在录像的(预录延录模式下).
     */
    override fun isUIRecording(): Boolean {
        logE("isUIRecording = ${mRecordView.isRecording()}")
        return mRecordView.isRecording()
    }

    /**
     * 开始自动对焦
     */
    override fun startAutoFocus(focusCallback: ICameraManager.AutoFocusCallback?) {
        mICameraManager.startAutoFocus(focusCallback)
    }

    /**
     * 设置自动对焦回调,若已设置,则[startAutoFocus]可以不再传入
     * @param focusCallback AutoFocusCallback?
     */
    override fun setAutoFocusCallback(focusCallback: ICameraManager.AutoFocusCallback?) {
        mICameraManager.setAutoFocusCallback(focusCallback)
    }

    /**
     * 停止自动对焦
     */
    override fun cancelAutoFocus() {
        mICameraManager.cancelAutoFocus()
    }

    /**
     * @param focusRect 设置定点对焦的区域
     */
    override fun focusOnRect(focusRect: Rect, focusCallback: ICameraManager.AutoFocusCallback?) {
        mICameraManager.focusOnRect(focusRect, focusCallback)
    }

    /**
     * @param x 在Camera窗口X相对坐标
     * @param y 在Camera窗口Y相对坐标
     * @param screenWidth Camera窗口的宽度
     * @param screenHeight Camera窗口的高度
     * @param horWidth 区域相对中心的左右扩展(-horWidth, horWidth)默认为100
     * @param verHeight 区域相对中心的上下扩展.(-verHeight, verHeight)默认为100
     */
    override fun focusOnPoint(x: Int, y: Int, screenWidth: Int, screenHeight: Int, horWidth: Int, verHeight: Int,
                              focusCallback: ICameraManager.AutoFocusCallback?) {
        mICameraManager.focusOnPoint(x, y, screenWidth, screenHeight, horWidth, verHeight, focusCallback)
    }

    override fun setCameraCallback(callback: ICameraPresenter.CameraStateCallback?) {
        Timber.v("setCameraCallback")
        mCameraStateCallback = callback
    }

    /**
     * @see [stopPreview]
     */
    override fun startPreview() {
        logE("startPreview().mSurface = $mSurface; mCameraOpened = ${mICameraManager.getCameraCore().isOpened()}")
//        mICameraManager.setDisplayOrientation(mRotation)
//        mICameraManager.setPictureRotation(mRotation)
        mSharedRender.startRender()
        mSurface?.apply {
            mICameraManager.startPreview(this)
        }
    }

    /**
     * @see [startPreview]
     */
    override fun stopPreview() {
        mICameraManager.stopPreview()
        mSharedRender.stopRender()
    }

    /**前提得先调用[setPictureCallback]方法来设置图片数据回调,否则拍照后图标数据不会被回传.
     */
    override fun takePicture() {
        mContext.sendBroadcast(Intent(LogReceiver.ACTION_CAPTURE))
        mRecordView.takePictureStart()
        mICameraManager.takePicture()
    }

    /**
     * 前提得先调用[setPictureCallback]方法来设置图片数据回调,否则拍照后图标数据不会被回传.
     * @param burstCount Int 高速连拍的照片数
     */
    override fun takeBurstPicture(burstCount: Int) {
        mContext.sendBroadcast(Intent(LogReceiver.ACTION_CAPTURE))
        mRecordView.takePictureStart()
        mICameraManager.takePictureBurst(burstCount)
    }

    /**
     * 设置照片的数据回调,[takePicture]/[takeBurstPicture]之前起码要设置一次,否则数据将会被回收.
     * @param callback ICameraManager.PictureDataCallback? 照片数据回调方法.
     */
    override fun setPictureCallback(callback: ICameraManager.PictureCallback?) {
//        mICameraManager.setPictureCallback(callback)
        mPictureCallback = callback
    }

    override fun getStartRecordTime(): Long {
        return mRecordStartTime
    }

    override fun getStopRecordTime(): Long {
        return mRecordStopTime
    }

    override fun getRecordView(): IRecordView {
        return mRecordView
    }

    override fun registerPreviewSurface(surface: Any, width: Int, height: Int, needCallback: Boolean, surfaceNeedRelease: Boolean) {
        mSharedRender.registerPreviewSurface(surface, width, height, needCallback, surfaceNeedRelease)
    }

    override fun unregisterPreviewSurface(surface: Any) {
        mSharedRender.unregisterPreviewSurface(surface)
    }

    override fun setOnFrameRenderListener(listener: SharedRender.OnFrameRenderListener?) {
        mSharedRender.setOnFrameRenderListener(listener)
    }

    /**
     * 若使用Camera1，则某些情况下需要手动重新预览。
     * */
    private fun checkNeedRestartPreview() {
        if (mIsCamera1)
            mICameraManager.startPreview()
    }

    abstract fun recordError(errorMsg: Int = R.string.record_error)

    abstract fun recordError(errorMsg: String)

    abstract fun recordFinished(file: File?): File?


    inner class RecordCallback : RecorderLooper.IRecordLoopCallback {

        override fun onRecorderPrepared() {
            mRecordCallback?.onRecorderPrepared()
        }

        override fun onRecordStart() {
            mRecordCallback?.onRecordStart()
            Observable.just(Unit)
                    .delay(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        mRecordView.enableCameraBtn()
                    }
        }

        /*override fun onRecorderConfigureFailed() {
            recordError(R.string.record_configure_error)
        }*/

        override fun onRecordError(errorCode: Int, errorType: Int) {
            Timber.e("onRecordError().errorCode = $errorCode")
            /*if (errorCode == IRecorder.IRecordCallback.RECORD_ERROR_TOO_SHORT) {
                recordError(R.string.record_too_short)
            } else {
                recordError(mContext.getString(R.string.record_error, errorCode))
            }*/
            mRecordView.stopRecord(false)
            when (errorCode) {
                IRecorder.IRecordCallback.RECORD_ERROR_TOO_SHORT -> recordError(R.string.record_too_short)
                MediaRecorder.MEDIA_ERROR_SERVER_DIED -> recordError(R.string.media_error_server_died)
                IRecorder.IRecordCallback.RECORD_ERROR_CONFIGURE_FAILED -> {
                    when (errorType) {
                        IRecorder.IRecordCallback.ERROR_CODE_FILE_WRITE_DENIED -> {
                            recordError(R.string.file_write_denied)
                        }
                        IRecorder.IRecordCallback.ERROR_CODE_CAMERA_SET_FAILED -> {
                            recordError(R.string.camera_set_failed)
                        }
                        else -> recordError(mContext.getString(R.string.record_error, errorCode))
                    }
                }
            }

            mRecordCallback?.onRecordError(errorCode, errorType)
        }

        override fun onRecordStop(stopCode: Int) {
            when (stopCode) {
                IRecorder.IRecordCallback.RECORD_STOP_EXTERNAL_STORAGE_NOT_ENOUGH -> {
                    recordError(R.string.storage_not_enough)
                }
                IRecorder.IRecordCallback.RECORD_STOP_EXTERNAL_STORAGE_NOT_MOUNTED -> {
                    recordError(R.string.external_storage_unmounted)
                }
            }
            mRecordCallback?.onRecordStop(stopCode)
            /*if (FileUtil.checkExternalStorageMounted(mContext)) {
                if (FileUtil.getDirFreeSpaceByMB(FileUtil.getExternalStoragePath(mContext)) <= 50) {
                    recordError(R.string.storage_not_enough)
                }
            } else {
                recordError(R.string.external_storage_unmounted)
            }*/
        }

        override fun onRecorderFinished(file: File?) {
            mRecordCallback?.onRecorderFinished(recordFinished(file))
        }

        override fun onRecordPause() {
            mRecordCallback?.onRecordPause()
        }

        override fun onRecordResume() {
            mRecordCallback?.onRecordResume()
        }

        override fun onLoopStart(startCode: Int) {
            mRecordCallback?.onLoopStart(startCode)
        }

        override fun onLoopStop(stopCode: Int) {
            mNeedLock = false
            when (stopCode) {
                RecorderLooper.IRecordLoopCallback.NORMAL -> {
                    if (mNeedCheckPreMode) {
                        mNeedCheckPreMode = false
                        checkPreRecordEnabled()
                    }
                }
            }
            mRecordCallback?.onLoopStop(stopCode)
        }

        override fun onLoopError(errorCode: Int) {
            mNeedLock = false
            mRecordCallback?.onLoopError(errorCode)
        }

        override fun onRecordStarting() {
            mRecordCallback?.onRecordStarting()
        }

        override fun onRecordStopping() {
            mRecordCallback?.onRecordStopping()
        }
    }


    inner class LockListener : IFileLocker.FileLockListener {

        override fun onLockStart() {

        }

        override fun onLockFinished() {
            mRecordView.lockRecordFinished(true)
        }

        override fun onLockFailed() {
            mRecordView.lockRecordFinished(false)
        }

    }

    private val mIntent by lazy {
        Intent(ACTION_STORAGE_CHANGE)
    }

    companion object {
        const val ACTION_STORAGE_CHANGE = "actionStorageChanged"
        const val STORAGE_PERCENT = "storagePercent"
    }

    inner class StorageCallback : StorageListener.StorageCallback {
        override fun onAvailablePercentChanged(percent: Int) {
            /*logE("percent = $percent")
            val percentValue = when {
                percent < 3 -> 0
                percent < 10 -> 5
                percent < 35 -> 25
                percent < 65 -> 50
                percent < 90 -> 75
                else -> 100
            }
            mIntent.apply {
                putExtra(STORAGE_PERCENT, percentValue)
                mContext.sendBroadcast(this)
            }*/
        }

        override fun onExternalStorageChanged(exist: Boolean, mounted: Boolean) {
            if (!HCameraSettings(mContext).isUseExternalStorage()) {
                return
            }
            CommonConst.FILE_ROOT_DIR = null
            Timber.d("onExternalStorageChanged.exist[$exist], mounted:[$mounted]")
            if (exist) {
                if (mounted) {
                    mRecorderLooper?.setDirPath(CommonConst.getVideoDir(mContext)!!.absolutePath)
                } else {
                    mRecorderLooper?.setDirPath(null)
                    TTSToast.showToast(R.string.external_storage_unmountable)
                }
//                startRecord()
            } else {
                if (isRecording())
                    stopRecord()
                mRecorderLooper?.setDirPath(null)
            }
        }
    }


    inner class PictureCallback : ICameraManager.PictureCallback {

        override fun onCaptureStart() {
            mPictureCallback?.onCaptureStart()
        }

        override fun onCaptureError(errorCode: Int) {
            mPictureCallback?.onCaptureError(errorCode)
        }

        override fun onCaptureResult(buffer: ByteArray) {
            mPictureCallback?.onCaptureResult(buffer)
//            checkNeedRestartPreview()
        }

        override fun onCaptureDone() {
            mPictureCallback?.onCaptureDone()
            mRecordView.takePictureFinish()
        }

    }

    inner class SurfaceListener : ISurfaceView.StateCallback<surface> {

        override fun onSurfaceDestroyed(surface: surface?) {
            logE("onSurfaceDestroyed")
            mSurfaceHolder?.apply {
                if (surface is SurfaceHolder) {
                    if (Global.NEED_CAMERA_FLOW_WINDOW) {
                        unregisterPreviewSurface((this as SurfaceHolder).surface)
                    }
                }
            }
            mSurfaceHolder = null
//            mICameraManager.closeCamera()
            mSurfaceCreated.set(false)
        }

        override fun onSurfaceCreate(surface: surface?) {
            synchronized(mObject) {
                logE("onSurfaceCreate. mCameraOpened = ${mICameraManager.getCameraCore().isOpened()}")
                mSurfaceHolder = surface
                surface?.apply {
                    if (Global.NEED_CAMERA_FLOW_WINDOW) {
                        registerPreviewSurface(
                            (this as SurfaceHolder).surface,
                            mSurfaceViewWidth,
                            mSurfaceViewHeight
                        )
                    }
                }
                /*if (mICameraManager.getCameraCore().canPreview()) {
                    startPreview()
                } else {
                    FlowableUtil.setBackgroundThread(
                            Consumer {
                                mICameraManager.openBackCamera()
                            }
                    )
                }*/
                mSurfaceCreated.set(true)
            }
        }

        override fun onSurfaceSizeChange(surface: surface?, width: Int, height: Int) {
            logE("onSurfaceSizeChange. [$width x $height]")
            mSurfaceViewWidth = width
            mSurfaceViewHeight = height
            if (Global.NEED_CAMERA_FLOW_WINDOW) {
                if (width == 0 || height == 0) {
                    surface?.apply {
                        if (surface is SurfaceHolder) {
                            unregisterPreviewSurface((this as SurfaceHolder).surface)
                        }
                    }
                } else {
                    surface?.apply {
                        registerPreviewSurface(
                            (this as SurfaceHolder).surface,
                            mSurfaceViewWidth,
                            mSurfaceViewHeight
                        )
                    }
                }
            }
//            mICameraManager.openSpecialCamera()
        }
    }

    inner class FocusCallback : ICameraManager.AutoFocusCallback {

        override fun onAutoFocusCallbackSuccess(success: Boolean) {
            logE("onAutoFocusCallbackSuccess = $success")
            mRecordView.focusSuccess(success)
        }

    }

    private fun logE(msg: String) {
        Timber.e(msg)
    }

    private fun logW(msg: String) {
        Timber.w(msg)
    }

    inner class CameraStateCallback : ICameraManager.CameraStateCallback<camera> {

        override fun onCameraOpening() {
            mCameraStateCallback?.onCameraOpening()
        }

        override fun onCameraOpenSuccess(camera: camera, id: Int) {
            synchronized(mObject) {
                Timber.d("onCameraOpenSuccess. mSurface = $mSurface")
                mCameraOpened.set(true)
                if (mIsCamera1) {
                    mRecorderLooper?.setCamera(camera)
                }
                initCameraParams()
                mCameraStateCallback?.onCameraOpenSuccess(id)
                checkCanPreview()
                /*try {
                    Observable.just(Unit)
                            .delay(mNeedDelay, TimeUnit.MILLISECONDS)
                            .subscribe {
                                mSurface.apply {
                                    startPreview()
                                }
                            }
                } catch (e: Exception) {
                    Observable.just(Unit)
                            .delay(500, TimeUnit.MILLISECONDS)
                            .subscribe {
                                mSurface.apply {
                                    startPreview()
                                }
                            }
                }*/
            }
        }

        override fun onCameraOpenFailed(errorCode: Int) {
            if (errorCode != ICameraManager.CAMERA_OPEN_ERROR_NOT_RELEASE) {
                mCameraOpened.set(false)
            }
            mCameraStateCallback?.onCameraOpenFailed(errorCode)
        }

        override fun onCameraPreviewSuccess() {
            logW("onCameraPreviewSuccess")
            mRecorderLooper?.startPreview()
            mCameraStateCallback?.onCameraPreviewSuccess()
        }

        override fun onCameraPreviewStop() {
            mCameraStateCallback?.onCameraPreviewStop()
        }

        override fun onCameraClosing() {
            mCameraStateCallback?.onCameraClosing()
        }

        override fun onCameraClosed() {
            mCameraOpened.set(false)
            if (mIsCamera1) {
                mRecorderLooper?.setCamera(null)
            }
            mCameraStateCallback?.onCameraClosed()
        }

        override fun onCameraErrorClose(errorCode: Int) {
//            logE("onCameraErrorClose. errorCode = $errorCode")
            stopRecord(isLoopRecording(), false)
            /*val errorMsg = when (errorCode) {
                Camera.CAMERA_ERROR_EVICTED -> {
                    R.string.camera_error_evicted
                }
                Camera.CAMERA_ERROR_SERVER_DIED -> {
                    R.string.media_error_server_died
                }
                else -> {
                    R.string.camera_error_unknown
                }
            }
            logE("onCameraErrorClose. errorCode = ${mContext.getString(errorMsg)}")
            recordError(errorMsg)*/
            mCameraStateCallback?.onCameraOpenFailed(errorCode)
            mCameraOpened.set(false)
        }
    }

    override fun onSurfaceTextureReady() {
        Timber.d("onSurfaceTextureReady()")
        checkCanPreview()
    }

    private fun checkCanPreview() {
        if (mSurface == null) {
            mSurface = mSharedRender.getSurfaceTexture()
        }
        if (mSurface != null && mCameraOpened.get()) {
            startPreview()
        }
    }
}