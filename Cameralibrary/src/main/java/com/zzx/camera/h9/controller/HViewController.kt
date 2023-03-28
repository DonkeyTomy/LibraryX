package com.zzx.camera.h9.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Camera
import android.os.*
import android.view.SurfaceHolder
import android.view.View
import android.widget.ImageView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import com.zzx.camera.ICameraStateCallback
import com.zzx.camera.IVideoRecordStateCallback
import com.zzx.camera.R
import com.zzx.camera.R2
import com.zzx.camera.component.CameraComponent
import com.zzx.camera.data.HCameraSettings
import com.zzx.camera.h9.addition.CaptureAddition
import com.zzx.camera.h9.addition.ICaptureAddition
import com.zzx.camera.h9.presenter.HCameraPresenter
import com.zzx.camera.h9.view.HSettingView
import com.zzx.camera.presenter.ICameraPresenter
import com.zzx.camera.presenter.IViewController
import com.zzx.camera.receiver.MessageReceiver
import com.zzx.camera.service.CameraService
import com.zzx.camera.values.CommonConst
import com.zzx.camera.values.Values
import com.zzx.media.camera.CameraCore
import com.zzx.media.camera.CameraCore.Status
import com.zzx.media.camera.ICameraManager
import com.zzx.media.recorder.IRecorder
import com.zzx.media.recorder.video.RecorderLooper.IRecordLoopCallback
import com.zzx.recorder.audio.IAudioRecordAIDL
import com.zzx.recorder.audio.IAudioRecordStateCallback
import com.zzx.utils.TTSToast
import com.zzx.utils.alarm.SoundPlayer
import com.zzx.utils.alarm.VibrateUtil
import com.zzx.utils.context.ContextUtil
import com.zzx.utils.event.EventBusUtils
import com.zzx.utils.file.FileUtil
import com.zzx.utils.power.WakeLockUtil
import com.zzx.utils.rxjava.FlowableUtil
import com.zzx.utils.zzx.SystemInfo
import com.zzx.utils.zzx.ZZXMiscUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**@author Tomy
 * Created by Tomy on 2018/10/4.
 */
class HViewController(var mContext: Context, private var mCameraPresenter: HCameraPresenter<SurfaceHolder, Camera>,
                      rootView: View, dagger: CameraComponent): IViewController, ICaptureAddition.ICaptureCallback,
    IAudioRecordStateCallback.Stub() {


    private var mAudioService: IAudioRecordAIDL? = null

//    private var mNeedRecordVideo = false

    @BindView(R2.id.btn_mode)
    lateinit var mBtnMode: ImageView

    @BindView(R2.id.btn_mode_switch)
    lateinit var mBtnModeSwitch: ImageView

    @BindView(R2.id.btn_rec)
    lateinit var mBtnCamera: ImageView

    @BindView(R2.id.iv_timer)
    lateinit var mTimerView: ImageView

    @BindView(R2.id.btn_camera_switch)
    lateinit var mBtnCameraSwitch: ImageView

    @BindView(R2.id.btn_ratio_switch)
    lateinit var mBtnRatio: ImageView

    @BindView(R2.id.btn_flash)
    lateinit var mBtnFlash: ImageView

    @BindView(R2.id.btn_laser)
    lateinit var mBtnLaser: ImageView

    @BindView(R2.id.btn_infrared)
    lateinit var mBtnInfrared: ImageView

    @BindView(R2.id.light_container)
    lateinit var mLightContainer: View

    @BindView(R2.id.top_bar)
    lateinit var mTopBar: View

    @BindView(R2.id.down_bar)
    lateinit var mDownBar: View

    @BindView(R2.id.btn_zoom_up)
    lateinit var mBtnZoomUp: View

    @BindView(R2.id.btn_zoom_down)
    lateinit var mBtnZoomDown: View

    private var mUnbinder: Unbinder

    private var mPictureFile: File? = null

    private var isRecordMode = true

    @Volatile
    private var isFlashOn = false

    @Volatile
    private var isLaserOn = false

    @Volatile
    private var isInfraredOn = false

    private val mIsThreeCamera by lazy { Camera.getNumberOfCameras() > 2 }

    private val mCameraCore = CameraCore<Camera>()

    private var mCameraNeedOpen = false

    private var mRemoteCameraCallbackList: RemoteCallbackList<ICameraStateCallback>? = null

    private var mRemoteRecordCallbackList: RemoteCallbackList<IVideoRecordStateCallback>? = null

    /***
     * 代表摄像头是否打开
     */
//    private val mCameraOpened = AtomicBoolean(false)

//    private val mCameraPreviewed = AtomicBoolean(false)

    private val mScreenOn   = AtomicBoolean(true)

//    private val mWindowMax  = AtomicBoolean(false)

    private val mCloseObject = Object()

    private val mEvent by lazy {
        ArrayBlockingQueue<Int>(1)
    }

    private val mWakeLock by lazy {
        WakeLockUtil(mContext)
    }

    private var mSwitchCamera = false

    private var mPreviewRetryCount = 0

    private var mNeedRecord = false

    private var mBootComplete = AtomicBoolean(true)

    private val mNeedCheck = true

    private var mPreScreenOff = false

    private val mSetting by lazy {
        HCameraSettings(mContext).apply {
            setCameraMode(HCameraSettings.DEFAULT_CAMERA_MODE_VIDEO)
        }
    }

    private var mSettingView: HSettingView? = null

    private lateinit var mCaptureAddition: ICaptureAddition

    private val mAutoInfraredReceiver by lazy {
        AutoInfraredReceiver()
    }

    init {
        dagger.inject(this)

        mUnbinder = ButterKnife.bind(this, rootView)
        init()
    }

    fun setAudioService(audioService: IAudioRecordAIDL?) {
        mAudioService = audioService
//        mAudioService?.registerRecordStateCallback(this)
        Timber.e("setAudioService.audioService = $audioService")
    }

    fun setRemoteCameraCallbackList(cameraCallbackList: RemoteCallbackList<ICameraStateCallback>?) {
        mRemoteCameraCallbackList = cameraCallbackList
    }

    fun setRemoteRecordCallbackList(recordCallbackList: RemoteCallbackList<IVideoRecordStateCallback>?) {
        mRemoteRecordCallbackList = recordCallbackList
    }

    override fun init() {
        mCaptureAddition = CaptureAddition(mContext, mBtnCamera, mSetting, mCameraPresenter, this, mTimerView)
        mSettingView = HSettingView(mContext, mBtnModeSwitch, mBtnRatio)
        mCameraPresenter.setCameraCallback(CameraStateCallback())
        mCameraPresenter.setRecordStateCallback(RecordCallback())
        refreshBtn()
        mContext.registerReceiver(mAutoInfraredReceiver, IntentFilter(ACTION_CAMERA_DARK).apply {
            addAction(ACTION_CAMERA_LIGHT)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
        })
    }

    private fun refreshBtn() {
        if (SystemInfo.getDeviceModel().contains("m9", true)) {
            mBtnInfrared.visibility = View.VISIBLE
            mBtnLaser.visibility    = View.VISIBLE
        }
        if (mCameraPresenter.getCameraCount() > 1) {
            mBtnCameraSwitch.visibility = View.VISIBLE
        }
    }

    override fun getCameraState(): Int {
        return mCameraCore.getStatus().ordinal.or(mCameraCore.getLoopStatus()).or(mCameraCore.getRecordStatue())
    }

    override fun isCapturing(): Boolean {
        return mCaptureAddition.isUserCapturing()
    }

    /**
     *
     * @param status Status
     * @param extraCode [ICameraManager.CAMERA_OPEN_ERROR_GET_INFO_FAILED]/[ICameraManager.CAMERA_OPEN_ERROR_OPEN_FAILED]/[ICameraManager.CAMERA_OPEN_ERROR_PREVIEW_FAILED]
     */
    @Synchronized
    fun cameraCallback(status: Status, extraCode: Int = 0, needSave: Boolean = true) {
        Timber.d("[CameraCore] cameraCallback() status = $status. extraCode = $extraCode")
        if (needSave && status != Status.ERROR) {
            mCameraCore.setStatus(status)
        }
        mRemoteCameraCallbackList?.apply {
            synchronized(mRemoteCameraCallbackList!!) {
                if (registeredCallbackCount < 1) {
                    return@apply
                }
                val size = beginBroadcast()
                Timber.w("cameraCallback.size = $size")
                for (i in 0 until size) {
                    getBroadcastItem(i).onCameraStateChanged(status.ordinal, extraCode)
                }
                finishBroadcast()
            }
        }
    }

    @Synchronized
    fun sendCaptureResult(state: Int, filePath: String?) {
        mRemoteCameraCallbackList?.apply {
            synchronized(mRemoteCameraCallbackList!!) {
                if (registeredCallbackCount < 1) {
                    return@apply
                }
                val size = beginBroadcast()
                Timber.w("cameraCallback.size = $size")
                for (i in 0 until size) {
                    getBroadcastItem(i).onCaptureResult(state, filePath)
                }
                finishBroadcast()
            }
        }
    }

    @Synchronized
    fun recordCallback(state: Int, value: String? = null, extraCode: Int = 0) {
        mRemoteRecordCallbackList?.apply {
            synchronized(mRemoteRecordCallbackList!!) {
                if (registeredCallbackCount < 1) {
                    return@apply
                }
                val size = beginBroadcast()
                Timber.w("recordCallback.size = $size")
                for (i in 0 until size) {
                    getBroadcastItem(i).onRecordStateChanged(state, value, extraCode)
                }
                finishBroadcast()
            }
        }
    }

    private fun oneShotFinish(file: File?) {
        if (mOneShot.get() && mPerformCapture) {
            mPerformCapture = false
            mOneShot.set(false)
            EventBusUtils.postEvent(MessageReceiver.ACTION_CAMERA, MessageReceiver.EXTRA_ONE_SHOT_FINISH)
            file?.apply {
                EventBusUtils.postEvent(CameraService.ACTION_ONE_SHOT, absolutePath)
            }
        }
    }

    inner class RecordCallback: IRecordLoopCallback {

        override fun onLoopStart(startCode: Int) {
            mCameraCore.setLoopRecord(true, startCode == IRecordLoopCallback.LOOP_AUTO_DELETE)
            recordCallback(IRecordLoopCallback.Status.LOOP_START.ordinal, extraCode = startCode)
        }

        override fun onLoopStop(stopCode: Int) {
            mCameraCore.setRecordImp(false)
            mCameraCore.setLoopRecord(false)
            recordCallback(IRecordLoopCallback.Status.LOOP_STOP.ordinal, extraCode = stopCode)
        }

        override fun onLoopError(errorCode: Int) {
            mCameraCore.setLoopRecord(false)
            mCameraCore.setRecordImp(false)
            recordCallback(IRecordLoopCallback.Status.LOOP_ERROR_STOP.ordinal, extraCode = errorCode)
        }

        override fun onRecorderPrepared() {
        }

        override fun onRecordStarting() {
//            cameraCallback(Status.RECORDING)
            recordCallback(IRecordLoopCallback.Status.RECORD_START.ordinal, extraCode = mCameraCore.getRecordImp())
        }

        override fun onRecordStart() {
            if (mPreScreenOff) {
                mPreScreenOff = false
                mWakeLock.screenOff(true)
            }
        }

        override fun onRecordStopping() {
        }

        override fun onRecordStop(stopCode: Int) {
//            mCameraCore.setRecordImp(false)
            cameraCallback(Status.PREVIEW)
            recordCallback(IRecordLoopCallback.Status.RECORD_STOP.ordinal, extraCode = stopCode)
        }

        override fun onRecordError(errorCode: Int, errorType: Int) {
//            mCameraCore.setRecordImp(false)
            mCameraCore.setStatus(Status.PREVIEW)
            recordCallback(IRecordLoopCallback.Status.RECORD_ERROR_STOP.ordinal, extraCode = errorCode)
        }

        override fun onRecorderFinished(file: File?) {
//            mCameraCore.setRecordImp(false)
            oneShotFinish(file)
            checkCameraNeedClose()
            recordCallback(IRecordLoopCallback.Status.RECORD_STOP.ordinal, file?.absolutePath)
        }

        override fun onRecordPause() {
        }

        override fun onRecordResume() {
        }

    }


    override fun setRotation(rotation: Int) {
        mCameraPresenter.setRotation(rotation)
    }

    /**
     * 切换录像状态
     */
    @OnClick(R2.id.btn_rec)
    override fun performCameraClick() {
        Timber.e("mCameraStatus = ${mCameraCore.getStatus()}")
        if (!checkCameraOpened(EVENT_CAPTURE)) {
            return
        }
        if (isRecordMode) {
           toggleRecord()
        } else {
            takePicture()
        }
    }


    override fun toggleRecord() {
        if (!checkCameraOpened(EVENT_RECORD)) {
            return
        }
        if (!isRecordMode) {
            switchToVideoMode()
        }
        mPerformCapture = true
        mCameraPresenter.apply {
            if (isUIRecording()) {
                if (stopRecord(enableCheckPreOrDelay = !mOneShot.get())) {
                    checkCameraNeedClose()
                }
            } else {
                this@HViewController.startRecord()
            }
        }
    }

    override fun performRecord(imp: Boolean) {
        if (mCaptureAddition.isUserCapturing())
            return
        Timber.d("imp = $imp")
        if (imp) {
            startRecord(true)
        } else {
            mPerformCapture = true
            toggleRecord()
        }
    }

    /**
     * @see stopRecord
     * @see toggleRecord
     * 开始录像
     */
    override fun startRecord(imp: Boolean) {
        Timber.d("CameraStatus is ${mCameraCore.getStatus()}. uiRecording = ${mCameraPresenter.isUIRecording()}")
        if (!checkCameraOpened(if (imp) EVENT_RECORD_IMP else EVENT_RECORD)) {
            Timber.e("checkCameraOpened fail. mCameraStatus = ${mCameraCore.getStatus()}")
            return
        }
        if (mCameraPresenter.isUIRecording() && !imp) {
            Timber.e("CameraPresenter is Recording. Do nothing")
            return
        }
        mPerformCapture = true
        if (mAudioService?.isRecording == true) {
//            Timber.e("audioRecordStartTime = ${mAudioService?.startTime}")
            if (SystemClock.elapsedRealtime() - (mAudioService?.startTime ?: 0) <= 2000) {
                return
            }
            if (mAudioService?.stopRecord() == true) {
//                mNeedRecordVideo = true
//                Timber.d("stopAudioRecord: $mNeedRecordVideo")
                Observable.just(Unit)
                        .delay(1000, TimeUnit.MILLISECONDS)
                        .subscribe {
                            controlRecordVideo(imp)
                        }
            } else {
                Timber.w("stopAudioRecord false")
            }
        } else {
            controlRecordVideo(imp)
        }

    }

    private fun controlRecordVideo(imp: Boolean) {
        if (AndPermission.hasPermissions(mContext, Permission.RECORD_AUDIO)) {
            performRecordVideo(imp)
        } else {
            AndPermission.with(mContext).runtime().permission(Permission.RECORD_AUDIO)
                    .onGranted {
                        it.forEach {
                            Timber.e("onGranted.action = $it")
                        }
                        performRecordVideo(imp)
                    }
                    .start()
        }
    }

    private fun performRecordVideo(imp: Boolean) {
        if (!isRecordMode) {
            switchToVideoMode()
        }
        releaseCameraClose()
        if (imp) {
            mCameraCore.setRecordImp(mCameraPresenter.toggleVideoIsImp())
            recordCallback(IRecordLoopCallback.Status.RECORD_START.ordinal, extraCode = mCameraCore.getRecordImp())
        } else {
//            mCameraCore.setRecordImp(false)
            mCameraPresenter.startRecord()
        }
    }

    override fun hideAllView() {
//        mLightContainer.visibility  = View.GONE
        mOneShot.set(false)
        mPerformCapture = false
        /*mBtnRatio.visibility    = View.GONE
        mTopBar.visibility      = View.GONE
        mDownBar.visibility     = View.GONE
        mBtnCamera.visibility   = View.GONE
        mBtnZoomDown.visibility = View.GONE
        mBtnZoomUp.visibility   = View.GONE*/
    }

    private val mOneShot = AtomicBoolean(false)

    @Volatile
    private var mPerformCapture = false

    override fun showAllView() {
//        mLightContainer.visibility  = View.VISIBLE
        if (mCaptureAddition.isIntervalOrDelayMode()) {
            mCaptureAddition.clearPictureMode(true)
        }
        mOneShot.set(false)
        mPerformCapture = false
        mBtnModeSwitch.visibility   = View.GONE
        refreshFlash()
        refreshInfrared()
        refreshLaser()
        /*mBtnRatio.visibility    = View.VISIBLE
        mTopBar.visibility      = View.VISIBLE
        mDownBar.visibility     = View.VISIBLE
        mBtnCamera.visibility   = View.VISIBLE
        mBtnZoomDown.visibility = View.VISIBLE
        mBtnZoomUp.visibility   = View.VISIBLE*/
    }

    /**
     * 停止录像
     */
    override fun stopRecord(enableCheckPreOrDelay: Boolean) {
        if (!mCameraPresenter.isUIRecording()) {
            Timber.e("CameraPresenter is Not Recording. Do nothing")
            return
        }
        if (!isRecordMode) {
            switchToVideoMode()
        }
        mCameraPresenter.stopRecord(enableCheckPreOrDelay = enableCheckPreOrDelay)
    }

    override fun release() {
        mCameraPresenter.release()
        mContext.unregisterReceiver(mAutoInfraredReceiver)
    }

    override fun releaseCamera() {
        mCameraPresenter.releaseCamera()
    }

    override fun showImpIcon(show: Boolean) {
    }

    /**
     * 拍照
     */
//    @OnClick(R2.id.btn_rec)
    override fun takePicture(needResult: Boolean, oneShot: Boolean) {
        if (!checkCameraOpened(EVENT_CAPTURE)) {
            return
        }
        mPerformCapture = true
        /*if (oneShot) {
            mOneShot.set(true)
        }*/
        releaseCameraClose()
        Flowable.just(Unit)
                .observeOn(Schedulers.newThread())
                .map {
                    return@map if (!mSetting.isUseExternalStorage()) {
                        if (mCaptureAddition.isIntervalOrDelayMode() || FileUtil.getDirFreeSpaceByMB(Environment.getExternalStorageDirectory()) > 50) {
                            1
                        } else {
                            0
                        }
                    } else {
                        when (FileUtil.getExternalStorageState(mContext)) {
                            Environment.MEDIA_MOUNTED     -> {
                                if (mCaptureAddition.isIntervalOrDelayMode() || FileUtil.getDirFreeSpaceByMB(
                                        FileUtil.getExternalStoragePath(mContext)
                                    ) > 50
                                ) {
                                    1
                                } else {
                                    0
                                }
                            }

                            Environment.MEDIA_UNMOUNTED   -> {
                                -1
                            }

                            Environment.MEDIA_UNMOUNTABLE -> {
                                -2
                            }

                            else                          -> {
                                -1
                            }
                        }
                    }
                }.observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Timber.e("takePicture.it = $it")
                    when (it) {
                        1   -> {
                            if (isRecordMode && !mCameraPresenter.mRecordView.isRecording())
                                switchToPhotoMode()
                            if (mOneShot.get() || oneShot) {
                                mCaptureAddition.takeOneShot(needResult)
                            } else {
                                mCaptureAddition.takePicture(needResult)
                            }
                        }
                        0   -> {
                            cameraCallback(Status.ERROR, CameraCore.ERROR_EXTRA_CODE_NOT_ENOUGH)
                            mCameraPresenter.recordError(R.string.storage_not_enough)
                            checkCameraNeedClose()
                            oneShotFinish(null)
                        }
                        -1  -> {
                            cameraCallback(Status.ERROR, CameraCore.ERROR_EXTRA_CODE_NOT_MOUNT)
                            mCameraPresenter.recordError(R.string.external_storage_unmounted)
                            checkCameraNeedClose()
                            oneShotFinish(null)
                        }
                        -2  -> {
                            mCameraPresenter.mRecordView.recordError(R.string.external_storage_unmountable)
                            checkCameraNeedClose()
                        }
                    }
                }

    }

    /**
     * @see checkCameraOpened
     * @param show Boolean
     */
    override fun showRecordingStatus(show: Boolean) {
//        mWindowMax.set(show)
        if (show) {
            releaseCameraClose()
//            Timber.e("showRecordingStatus.mCameraStatus = ${mCameraCore.getStatus()}, isSurfaceCreated = ${mCameraPresenter.isSurfaceCreated()}")
            if (mCameraPresenter.isSurfaceCreated()) {
                Timber.e("showRecordingStatus. openCamera")
                if (mCameraCore.canOpen()) {
                    reopenCamera()
                } else if (mCameraCore.isClosing()) {
                    mCameraNeedOpen = true
                }
            }
        } else {
            checkCameraNeedClose()
        }
        mCameraPresenter.showRecordingStatus(show)
    }

    private fun reopenCamera() {
        Timber.e("checkCameraOpened. reopenCamera")
        when (mCameraId) {
            CAMERA_ID_FRONT -> {
                mCameraPresenter.openFrontCamera()
            }
            CAMERA_ID_PHOTO -> {
                mCameraPresenter.openSpecialCamera()
            }
            CAMERA_ID_REC -> {
                mCameraPresenter.openBackCamera()
            }
        }
    }

    override fun configurationChanged() {
        mSettingView?.configurationChanged()
    }

    override fun onCaptureDone(file: File?) {
        sendCaptureResult(Status.CAPTURE_RESULT.ordinal, file?.absolutePath)
        mCameraPresenter.showThumb(file)
        oneShotFinish(file)
    }

    private fun sendResult(file: File?) {
        mContext.sendBroadcast(Intent(CameraService.ACTION_ONE_SHOT).apply {
            putExtra("file", file!!.absolutePath)
        })
    }

    override fun onCaptureFinish() {
        if (!mCameraCore.isRecording() && !mCameraCore.isNormalLoop()) {
            cameraCallback(Status.PREVIEW)
            sendCaptureResult(Status.CAPTURE_FINISH.ordinal, null)
        } else {
            cameraCallback(Status.RECORDING)
        }
        checkCameraNeedClose()
    }

    override fun onCaptureStart() {
        cameraCallback(if (mCameraCore.isRecording() || mCameraCore.isNormalLoop()) Status.RECORDING_CAPTURING else Status.CAPTURING)
    }

    override fun onCaptureError(errorCode: Int) {
        if (errorCode == IRecorder.IRecordCallback.ERROR_CODE_FILE_WRITE_DENIED) {
            TTSToast.showToast(R.string.file_write_denied)
        }
    }

    override fun onUserCaptureStart() {
        cameraCallback(if (mCameraCore.isRecording() || mCameraCore.isNormalLoop()) Status.RECORDING_CAPTURING else Status.CAPTURING, needSave = false)
    }

    /**
     * @see showRecordingStatus
     * @param event Int
     * @return Boolean
     */
    fun checkCameraOpened(event: Int): Boolean {
        Timber.e("checkCameraOpened. event = $event. mCameraStatus = ${mCameraCore.getStatus()}. mSurfaceCreated = ${mCameraPresenter.isSurfaceCreated()}")
//        if (mCameraPresenter.isSurfaceCreated()) {
            if (mCameraCore.canOpen() || mCameraCore.isClosing() || mCameraCore.isOpening() || mCameraCore.isOpened()) {
                if (event != EVENT_NONE) {
                    mEvent.clear()
                    mEvent.add(event)
                }
            }
            if (mCameraCore.canOpen()) {
                reopenCamera()
            } else if (mCameraCore.isClosing()) {
                Timber.e("checkCameraOpened. mCameraNeedOpen")
                mCameraNeedOpen = true
            }
//        }
        /*if (event != EVENT_NONE && !mCameraCore.isPreview()) {
            mEvent.clear()
            mEvent.add(event)
        }*/
        return mCameraCore.canCapture()
    }

    /**
     * 录像/拍照 模式切换
     */
    @OnClick(R2.id.btn_mode)
    fun switchMode() {
        if (mCameraCore.canOpen() || mCameraCore.isBusy()) {
            return
        }
        if (isRecordMode) {
            SoundPlayer.getInstance().playSound(mContext, R.raw.pic_mode)
            switchToPhotoMode()
        } else {
            SoundPlayer.getInstance().playSound(mContext, R.raw.record_mode)
            switchToVideoMode()
        }
    }

    private fun switchToPhotoMode() {
        isRecordMode = false
        mBtnMode.setImageResource(R.drawable.btn_mode_video)
        mBtnCamera.setImageResource(R.drawable.btn_take_photo)
        mBtnModeSwitch.setImageResource(R.drawable.topbar_mode_icon)
        mSetting.setCameraMode(0)
        mSettingView?.refreshRatioButton()
    }

    private fun switchToVideoMode() {
        /*if (mCaptureAddition.isIntervalOrDelayMode()) {
            mCaptureAddition.clearPictureMode(false)
        }*/
        isRecordMode = true
        mBtnMode.setImageResource(R.drawable.btn_mode_photo)
        mBtnModeSwitch.setImageResource(R.drawable.topbar_list_icon)
        mSetting.setCameraMode(HCameraSettings.DEFAULT_CAMERA_MODE_VIDEO)
        mSettingView?.refreshRatioButton()
        mBtnCamera.setImageResource(R.drawable.btn_record)
    }

    @OnClick(R2.id.btn_mode_switch)
    fun showCameraModeSetting() {
        if (mCameraCore.canOpen() || mCameraCore.isClosing() || mCameraCore.isCapturing()) {
            return
        }
        if (isRecordMode) {
            mSettingView?.showVideoMode()
        } else {
            if (checkBackgroundRecording()) {
                return
            }
            mSettingView?.showPhotoMode()
        }
    }

    private fun switchPicVid() {
        if (mCameraPresenter.getCameraCount() > 2) {
            mCameraPresenter.releaseCamera()
        }
    }

    /**
     * 切换摄像头
     */
    @OnClick(R2.id.btn_camera_switch)
    override fun switchCamera() {
        if (checkBackgroundRecording() || mCameraCore.isBusy()) {
            return
        }
        Timber.i("switchCamera()")
        mBtnCameraSwitch.isClickable = false
        if (mCameraPresenter.getCameraCount() > 1) {
            mSwitchCamera = true
            releaseCamera()
        } else {
            Timber.e("Only have One Camera")
        }
    }

    fun checkBackgroundRecording(): Boolean {
        return if (mCameraPresenter.isRecording()) {
            VibrateUtil(mContext).start()
            mCameraPresenter.getRecordView().showMsg(R.string.record_pre_disable_first)
            true
        } else {
            false
        }
    }

    inner class CameraStateCallback: ICameraPresenter.CameraStateCallback {

        override fun onCameraOpening() {
            cameraCallback(Status.OPENING)
        }

        override fun onCameraClosing() {
            cameraCallback(Status.CLOSING)
        }

        override fun onCameraErrorClose(errorCode: Int) {
            cameraCallback(Status.RELEASE, errorCode)
        }

        override fun onCameraClosed() {
            cameraCallback(Status.RELEASE)
//            mCameraOpened.set(false)
//            mCameraPreviewed.set(false)
            Timber.e("onCameraClosed.mCameraStatus = ${mCameraCore.getStatus()}; mSwitchCamera = $mSwitchCamera")
            if (mSwitchCamera) {
                mSwitchCamera = false
                mPreviewRetryCount = 0
                when (mCameraId) {
                    CAMERA_ID_REC   -> {
                        switchToVideoMode()
                        mCameraId = CAMERA_ID_FRONT
                        mBtnCameraSwitch.setImageResource(R.drawable.front)
                        mSettingView?.switchCamera(Camera.CameraInfo.CAMERA_FACING_FRONT)
                        FlowableUtil.setBackgroundThread(
                                Consumer {
                                    mCameraPresenter.openFrontCamera()
                                }
                        )
                        mBtnFlash.visibility = View.INVISIBLE
                        mBtnLaser.visibility = View.INVISIBLE
                        mBtnInfrared.visibility = View.INVISIBLE
                    }
                    CAMERA_ID_FRONT -> {
                        mCameraId = if (mIsThreeCamera) CAMERA_ID_PHOTO else CAMERA_ID_REC
                        mSettingView?.switchCamera(Camera.CameraInfo.CAMERA_FACING_BACK)
                        if (mIsThreeCamera) {
                            switchToPhotoMode()
                            mBtnCameraSwitch.setImageResource(R.drawable.photo)
                            mCameraPresenter.openSpecialCamera()
                            mBtnFlash.visibility = View.VISIBLE
                            mBtnLaser.visibility = View.VISIBLE
                            mBtnInfrared.visibility = View.INVISIBLE
                        } else {
                            switchToVideoMode()
                            mBtnCameraSwitch.setImageResource(R.drawable.night)
                            mCameraPresenter.openBackCamera()
                            mBtnFlash.visibility = View.VISIBLE
                            mBtnLaser.visibility = View.VISIBLE
                            mBtnInfrared.visibility = View.VISIBLE
                        }

                    }
                    CAMERA_ID_PHOTO -> {
                        mCameraId = CAMERA_ID_REC
                        switchToVideoMode()
                        mBtnCameraSwitch.setImageResource(R.drawable.night)
                        mSettingView?.switchCamera(Camera.CameraInfo.CAMERA_FACING_BACK)
                        mCameraPresenter.openBackCamera()
                        mBtnFlash.visibility = View.VISIBLE
                        mBtnLaser.visibility = View.VISIBLE
                        mBtnInfrared.visibility = View.VISIBLE

                    }
                }
            } else if (mCameraNeedOpen) {
                mCameraNeedOpen = false
                reopenCamera()
            }
        }

        override fun onCameraOpenSuccess(id: Int) {
            cameraCallback(Status.OPENED, extraCode = id)
            mPreScreenOff = !mWakeLock.screenOn()
        }

        override fun onCameraPreviewStop() {
        }

        override fun onCameraPreviewSuccess() {
            mPreviewRetryCount = 0
            Observable.just(Unit)
                    .delay(300, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        Timber.w("onCameraPreviewSuccess.")
                        mBtnCameraSwitch.isClickable = true
                    }
                        cameraCallback(Status.PREVIEW)
//                        mCameraPreviewed.set(true)
                        if (mBootComplete.get()) {
                            Timber.e("first Open()")
                            mBootComplete.set(false)
                            mSetting.apply {
                                if (getRecordAuto()) {
                                    startRecord()
                                } else {
                                    mCameraPresenter.checkPreRecordEnabled()
                                }
                            }
                        } else {
                            val event = mEvent.poll()
                            Timber.i("event = $event")
                            if (event != null) {
                                when (event) {
                                    EVENT_CAPTURE   -> takePicture()
                                    EVENT_RECORD    -> startRecord()
                                    EVENT_RECORD_IMP    -> startRecord(true)
                                }
                            }
                        }
//                    }
        }

        override fun onCameraOpenFailed(errorCode: Int) {
            Timber.e("onCameraOpenFailed.errorCode = $errorCode")
            if (errorCode == ICameraManager.CAMERA_OPEN_ERROR_NOT_RELEASE) {
                Observable.just(Unit)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            mBtnCameraSwitch.isClickable = true
                        }
                return
            }
//            mCameraOpened.set(false)
//            mCameraPreviewed.set(false)
            Timber.e("onCameraOpenFailed.mCameraStatus = ${mCameraCore.getStatus()}")
            val errorMsg = when (errorCode) {
                ICameraManager.CAMERA_OPEN_ERROR_NOT_RELEASE    -> {
                    R.string.camera_not_release
                }

                ICameraManager.CAMERA_OPEN_ERROR_NO_CAMERA      -> {
//                    cameraCallback(Status.RELEASE, errorCode)
                    R.string.camera_not_exist
                }

                ICameraManager.CAMERA_OPEN_ERROR_OPEN_FAILED    -> {
//                    cameraCallback(Status.RELEASE, errorCode)
                    checkCameraRetryFailed(R.string.camera_open_failed)
                }
                ICameraManager.CAMERA_OPEN_ERROR_GET_INFO_FAILED -> {
//                    cameraCallback(Status.RELEASE, errorCode)
                    checkCameraRetryFailed(R.string.camera_open_failed_get_info_failed)
                }

                ICameraManager.CAMERA_OPEN_ERROR_PREVIEW_FAILED -> {
//                    cameraCallback(Status.RELEASE, errorCode)
                    checkCameraRetryFailed(R.string.camera_preview_failed)
                }
                Camera.CAMERA_ERROR_UNKNOWN -> {
                    checkCameraRetryFailed(R.string.camera_error_unknown)
                }
                Camera.CAMERA_ERROR_SERVER_DIED -> {
                    checkCameraRetryFailed(R.string.camera_error_server_died)
                }
                Camera.CAMERA_ERROR_EVICTED -> {
                    checkCameraRetryFailed(R.string.camera_error_evicted)
                }
                else -> {
                    Observable.just(Unit)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                mBtnCameraSwitch.isClickable = true
                            }
                   -1
                }
            }
            if (errorMsg != -1)
                Observable.just(Unit)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            cameraCallback(Status.RELEASE, errorCode)
                            mCameraPresenter.recordError(errorMsg)
                            mBtnCameraSwitch.isClickable = true
                        }
        }

    }

    private fun checkCameraRetryFailed(failedMsg: Int): Int {
        return if (++mPreviewRetryCount < PREVIEW_RETRY_MAX_COUNT) {
            Observable.timer(500, TimeUnit.MILLISECONDS)
                    .subscribe {
                        when (mCameraId) {
                            CAMERA_ID_REC -> {
                                mCameraPresenter.openBackCamera()
                            }
                            CAMERA_ID_PHOTO -> {
                                mCameraPresenter.openSpecialCamera()
                            }
                            CAMERA_ID_FRONT -> {
                                mCameraPresenter.openFrontCamera()
                            }
                        }
                    }
            -1
        } else {
            mPreviewRetryCount = 0
            failedMsg
        }
    }

    /**
     * 打开文件管理器
     */
    @OnClick(R2.id.btn_thumb)
    fun openFileDir() {
        EventBusUtils.postEvent(MessageReceiver.ACTION_CAMERA, MessageReceiver.EXTRA_DISMISS_WIN)
        ContextUtil.startOtherActivity(mContext, Values.PACKAGE_FILE, Values.CLASS_FILE, Bundle().apply {
            putInt(CommonConst.INTENT_FROM, CommonConst.FROM_CAMERA)
            mCameraPresenter.mFile?.let {
                val fileType = when (it.extension) {
                    "mp4", "avc" -> {
                        CommonConst.FileType.VIDEO.ordinal
                    }
                    "png", "jpg" -> {
                        CommonConst.FileType.IMAGE.ordinal
                    }
                    else -> {-1}
                }
                if (fileType != -1) {
                    putInt(CommonConst.FILE_TYPE, fileType)
                    putString(CommonConst.FILE_PATH, it.absolutePath)
                }
            }
        })
    }

    @OnClick(R2.id.btn_ratio_switch)
    fun showRation() {
        if (mCameraCore.isBusy()) {
            return
        }
        if (checkBackgroundRecording()) {
            return
        }
        if (isRecordMode) {
            mSettingView?.showVideoRatio()
        } else {
            mSettingView?.showPhotoRatio()
        }
    }

    @OnClick(R2.id.btn_zoom_up)
    fun zoomUp() {
        if (mCameraCore.canOpen()) {
            return
        }
        mCameraPresenter.zoomUp(2)
    }

    @OnClick(R2.id.btn_zoom_down)
    fun zoomDown() {
        if (mCameraCore.canOpen()) {
            return
        }
        mCameraPresenter.zoomDown(2)
    }

    private fun refreshFlash() {
        disableBtn(mBtnFlash)
//        isFlashOn = ZZXMiscUtils.read(ZZXMiscUtils.FLASH_PATH)?.equals(ZZXMiscUtils.OPEN) ?: false
        mBtnFlash.setImageResource(
                if (!isFlashOn)
                    R.drawable.btn_flash_off
                else
                    R.drawable.btn_flash_on
        )
    }

    private fun refreshLaser() {
        disableBtn(mBtnLaser)
        isLaserOn = ZZXMiscUtils.read(ZZXMiscUtils.LASER_PATH)?.equals(ZZXMiscUtils.OPEN) ?: false
        mBtnLaser.setImageResource(
                if (!isLaserOn)
                    R.drawable.btn_laser_off
                else
                    R.drawable.btn_laser_on
        )
    }

    private fun refreshInfrared() {
        disableBtn(mBtnInfrared, 1000)
        isInfraredOn = ZZXMiscUtils.read(ZZXMiscUtils.IR_CUT_PATH)?.equals(ZZXMiscUtils.OPEN) ?: false
        mBtnInfrared.setImageResource(
                if (!isInfraredOn)
                    R.drawable.btn_infrared_off
                else
                    R.drawable.btn_infrared_on
        )
    }

    @OnClick(R2.id.btn_flash)
    fun switchFlash() {
        disableBtn(mBtnFlash)
        isFlashOn = !isFlashOn
        ZZXMiscUtils.setFlashState(isFlashOn)
        mBtnFlash.setImageResource(
                if (!isFlashOn)
                    R.drawable.btn_flash_off
                else
                    R.drawable.btn_flash_on
        )
        Timber.e("isFlashOn = $isFlashOn")
        EventBusUtils.postEvent(CameraService.ACTION_CAMERA_REFRESH_LIGHT, CameraService.REFRESH_FLASH)
    }

    private fun disableBtn(btn: View, delayTime: Long = 500) {
        Observable.just(btn)
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    btn.isClickable = false
                }
                .delay(delayTime, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    btn.isClickable = true
                }
    }

    @OnClick(R2.id.btn_laser)
    fun switchLaser() {
        disableBtn(mBtnLaser)
        isLaserOn = !isLaserOn
        ZZXMiscUtils.setLaserState(isLaserOn)
        mBtnLaser.setImageResource(
                if (!isLaserOn)
                    R.drawable.btn_laser_off
                else
                    R.drawable.btn_laser_on
        )
        Timber.e("isLaserOn = $isLaserOn")
        EventBusUtils.postEvent(CameraService.ACTION_CAMERA_REFRESH_LIGHT, CameraService.REFRESH_LASER)
    }

    @OnClick(R2.id.btn_infrared)
    fun switchInfrared() {
        disableBtn(mBtnInfrared, 700)
        isInfraredOn = (ZZXMiscUtils.read(ZZXMiscUtils.IR_CUT_PATH)?.equals(ZZXMiscUtils.OPEN) ?: false).not()
        ZZXMiscUtils.setIrRedState(isInfraredOn)
        mBtnInfrared.setImageResource(
                if (!isInfraredOn)
                    R.drawable.btn_infrared_off
                else
                    R.drawable.btn_infrared_on
        )
        Timber.e("isInfraredOn = $isInfraredOn")
        EventBusUtils.postEvent(CameraService.ACTION_CAMERA_REFRESH_LIGHT, CameraService.REFRESH_INFRARED)
    }

    inner class AutoInfraredReceiver: BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            Timber.w("HViewController: action = ${intent.action}")
            when (intent.action) {
                ACTION_CAMERA_DARK  -> {
                    if (isInfraredOn) {
                        return
                    }
                    disableBtn(mBtnInfrared, 700)
                    FlowableUtil.setMainThreadMapBackground<Unit>(
                             {
                                mBtnInfrared.setImageResource(R.drawable.btn_infrared_on)
                            },
                            Consumer {
                                isInfraredOn = true
                                ZZXMiscUtils.setIrRedState(true)
                            }
                    )
                }
                ACTION_CAMERA_LIGHT -> {
                    if (!isInfraredOn) {
                        return
                    }
                    disableBtn(mBtnInfrared, 700)
                    FlowableUtil.setMainThreadMapBackground<Unit>(
                        {
                            mBtnInfrared.setImageResource(R.drawable.btn_infrared_off)
                        },
                        {
                            isInfraredOn = false
                            ZZXMiscUtils.setIrRedState(false)
                        }
                    )
                }
                Intent.ACTION_SCREEN_OFF    -> {
                    mWakeLock.lock()
                    mScreenOn.set(false)
                    checkCameraNeedClose()
                }
                Intent.ACTION_SCREEN_ON     -> {
                    mWakeLock.unlockScreen()
                    mWakeLock.releaseLock()
                    mScreenOn.set(true)
                    releaseCameraClose()
                    if (mCameraPresenter.isSurfaceCreated()) {
                        checkCameraOpened(EVENT_NONE)
                    }
                }
            }
        }

    }

    private var mCameraCloseDisposable: Disposable? = null

    private val mCameraCloseChecker by lazy {
        Observable.just(Unit)
                .delay(CAMERA_CLOSE_DELAY, TimeUnit.SECONDS)
                .observeOn(Schedulers.single())
                .map {
                    synchronized(mCloseObject) {
//                        val needRelease = mCameraCloseDisposable?.isDisposed != true && (!(mCameraPresenter.isRecording() || mCaptureAddition.isCapturing()))
                        val needRelease = !(mCameraPresenter.isRecording() || mCaptureAddition.isUserCapturing())
                        Timber.w("CameraCloseChecker.needRelease = $needRelease")
                        if (needRelease) {
                            mCameraPresenter.releaseCamera()
                        }
                    }
                }
    }

    fun checkCameraNeedClose() {
        if (!mNeedCheck || (mScreenOn.get() && mCameraPresenter.isSurfaceCreated() || mCameraPresenter.isRecording() || mCaptureAddition.isUserCapturing())) {
            Timber.w("checkCameraNeedClose cancel")
            return
        }
        Timber.d("checkCameraNeedClose()")
        synchronized(mCloseObject) {
            mCameraCloseDisposable?.dispose()
            mCameraCloseDisposable = mCameraCloseChecker.subscribe()
        }
    }

    fun releaseCameraClose() {
        if (!mNeedCheck) {
            return
        }
        Timber.w("releaseCameraClose()")
        synchronized(mCloseObject) {
            if (mCameraCloseDisposable?.isDisposed != true)
                mCameraCloseDisposable?.dispose()
        }
    }

    companion object {
        const val CAMERA_ID_REC     = 0
        const val CAMERA_ID_FRONT   = 1
        const val CAMERA_ID_PHOTO   = 2

        var mCameraId = CAMERA_ID_REC

        const val ACTION_CAMERA_DARK = "zzx_action_camera_dark"
        const val ACTION_CAMERA_LIGHT = "zzx_action_camera_light"

        //预览失败重启最大次数
        const val PREVIEW_RETRY_MAX_COUNT = 3

        const val EVENT_NONE    = 0
        const val EVENT_CAPTURE = 1
        const val EVENT_RECORD  = 2
        const val EVENT_RECORD_IMP  = 3

        const val CAMERA_CLOSE_DELAY    = 20L
    }

    override fun onRecordStart() {
    }

    override fun onRecordStop(filePath: String?) {
        /*Timber.d("onAudioRecordStop: $filePath; mNeedRecordVideo = $mNeedRecordVideo")
        if (mNeedRecordVideo) {
            mNeedRecordVideo = false
            controlRecordVideo(false)
        }*/
    }

    override fun onRecordError(code: Int) {
    }

}