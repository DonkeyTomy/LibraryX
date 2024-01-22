package com.zzx.camera.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.*
import android.content.res.Configuration
import android.hardware.Camera
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.RemoteCallbackList
import android.os.SystemClock
import android.view.KeyEvent
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import androidx.core.app.NotificationCompat
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.tomy.lib.ui.manager.FloatWinManager
import com.tomy.lib.ui.view.layout.MainLayout
import com.zzx.camera.*
import com.zzx.camera.component.DaggerCameraComponent
import com.zzx.camera.data.HCameraSettings
import com.zzx.camera.h9.controller.HViewController
import com.zzx.camera.h9.module.HCameraModule
import com.zzx.camera.h9.presenter.HCameraPresenter
import com.zzx.camera.module.CameraModule
import com.zzx.camera.presenter.IViewController
import com.zzx.camera.qualifier.FloatWinContainer
import com.zzx.camera.receiver.MessageReceiver
import com.zzx.camera.values.Values
import com.zzx.log.LogReceiver
import com.zzx.media.camera.CameraCore
import com.zzx.media.camera.ICameraManager
import com.zzx.media.custom.view.opengl.renderer.SharedRender
import com.zzx.media.values.TAG
import com.zzx.recorder.audio.IAudioRecordAIDL
import com.zzx.recorder.audio.service.RecordService
import com.zzx.utils.TTSToast
import com.zzx.utils.alarm.SoundPlayer
import com.zzx.utils.alarm.VibrateUtil
import com.zzx.utils.context.ContextUtil
import com.zzx.utils.event.EventBusUtils
import com.zzx.utils.file.FileUtil
import com.zzx.utils.rxjava.fixedThread
import com.zzx.utils.zzx.LedController
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.math.abs

/**@author Tomy
 * Created by Tomy on 2018/5/31.
 */
class CameraService: Service() {

    @field:FloatWinContainer
    @Inject
    lateinit var mFloatCameraManager: FloatWinManager

    @field:FloatWinContainer
    @Inject
    lateinit var mContainer: View

    @Inject
    lateinit var mCameraPresenter: HCameraPresenter<SurfaceHolder, Camera>

    private var mPictureFile: File? = null

    private var mViewController: IViewController? = null

    /**
     * 上一个状态是否在录像中
     */
    private var mPreRecording = false

    /** 拍照后是否需要将路径返回 **/
    private val mPicNeedResult = AtomicBoolean(false)

    /** 录像后是否需要将路径返回 **/
    private val mVideoNeedResult = AtomicBoolean(false)
    @Volatile
    private var mInfraredOpened = false

    private val mCameraSettings by lazy {
        HCameraSettings(this)
    }

    private val mReceiver by lazy {
        HomeReceiver()
    }

    private val mLogReceiver by lazy {
        LogReceiver(this)
    }

    @Volatile
    private var mRenderListenerRegistered = false

    @Volatile
    private var mPreviewCallbackRegistered = false

    private val mFrameRenderListener by lazy { FrameRenderListener() }

    private val mPreviewCallback by lazy { PreviewCallback() }

    private var mAudioService: IAudioRecordAIDL? = null

    private val mAudioConnection by lazy {
        AudioServiceConnection()
    }

    private val mRemoteCameraCallbackList by lazy {
        RemoteCallbackList<ICameraStateCallback>()
    }

    private val mRemoteRecordCallbackList by lazy {
        RemoteCallbackList<IVideoRecordStateCallback>()
    }

    private val mRemoteRenderCallbackList by lazy {
        RemoteCallbackList<IFrameRenderCallback>()
    }

    private val mRenderMap by lazy {
        ConcurrentHashMap<Int, IFrameRenderCallback>()
    }

    private val mRemotePreviewCallbackList by lazy {
        RemoteCallbackList<IPreviewCallback>()
    }

    private val mIBinder = object : ICameraServiceAIDL.Stub() {

        override fun zoomUp() {
            this@CameraService.zoomUp()
        }

        override fun startRecord(needResult: Boolean, oneShor: Boolean) {
            this@CameraService.performRecord()
        }

        override fun setSensorOrientation(orientation: Int) {
            this@CameraService.setSensorOrientation(orientation)
        }

        override fun zoomDown() {
            this@CameraService.zoomDown()
        }

        override fun getZoomMax(): Int {
            return this@CameraService.getZoomMax()
        }

        override fun zoomLevel(level: Int) {
            this@CameraService.setZoomLevel(level)
        }

        override fun flashOn(): Boolean {
            this@CameraService.setFlashOn()
            return true
        }

        override fun flashOff(): Boolean {
            this@CameraService.setFlashOff()
            return true
        }

        override fun isSurfaceRegistered(surface: Surface?): Boolean {
            return false
        }

        override fun unregisterPreviewSurface(surface: Surface): Int {
            this@CameraService.unregisterPreviewSurface(surface)
            return 0
        }

        override fun registerPreviewSurface(surface: Surface, width: Int, height: Int, rendCallback: IFrameRenderCallback?, surfaceNeedRelease: Boolean): Int {
            Timber.i("registerPreviewSurface. rendCallback = $rendCallback")
            this@CameraService.registerPreviewSurface(surface, width, height, rendCallback, surfaceNeedRelease)
            return 0
        }

        override fun hideControlView() {
            mViewController?.hideAllView()
        }

        override fun showControlView() {
            mViewController?.showAllView()
        }

        override fun registerPreviewCallback(callback: IPreviewCallback) {
            synchronized(mRemotePreviewCallbackList) {
                mRemotePreviewCallbackList.register(callback)
                if (!mPreviewCallbackRegistered) {
                    mPreviewCallbackRegistered = true
                    mCameraPresenter.setPreviewCallback(mPreviewCallback)
                }
            }
        }

        override fun unregisterPreviewCallback(callback: IPreviewCallback) {
            synchronized(mRemotePreviewCallbackList) {
                mRemotePreviewCallbackList.unregister(callback)
                if (mRemotePreviewCallbackList.registeredCallbackCount <= 0) {
                    stopPreviewBroadcast()
                    mPreviewCallbackRegistered = false
                    mCameraPresenter.setPreviewCallback(null)
                }
            }
        }

        /*override fun registerFrameRenderCallback(renderCallback: IFrameRenderCallback, surface: Surface) {
            synchronized(mRemoteRenderCallbackList) {
                val id = System.identityHashCode(surface)
                Timber.tag(TAG.SURFACE_ENCODER).i("registerFrameRenderCallback. hashCode = ${mRenderMap[id]}")
                if (mRenderMap[id] == null) {
                    mRenderMap[id] = renderCallback
                    stopFrameRenderCallback()
                    mRemoteRenderCallbackList.register(renderCallback)
                    if (!mRenderListenerRegistered) {
                        mRenderListenerRegistered = true
                        mCameraPresenter.setOnFrameRenderListener(mFrameRenderListener)
                    }
                }
            }
        }

        override fun unregisterFrameRenderCallback(renderCallback: IFrameRenderCallback) {
            synchronized(mRemoteRenderCallbackList) {
                Timber.tag(TAG.SURFACE_ENCODER).i("unregisterFrameRenderCallback.")
//                mRenderMap.remove(renderCallback)
                if (mRenderMap.isEmpty()) {
                    mRenderListenerRegistered = false
                    mCameraPresenter.setOnFrameRenderListener(null)
                }
                stopFrameRenderCallback()
                mRemoteRenderCallbackList.unregister(renderCallback)
            }
        }*/

        override fun registerCameraStateCallback(cameraStateCallback: ICameraStateCallback) {
            Timber.w("registerCameraStateCallback")
            mRemoteCameraCallbackList.register(cameraStateCallback)
        }

        override fun unregisterCameraStateCallback(cameraStateCallback: ICameraStateCallback) {
            mRemoteCameraCallbackList.unregister(cameraStateCallback)
        }

        override fun registerRecordStateCallback(recordCallback: IVideoRecordStateCallback) {
            Timber.w("registerRecordStateCallback")
            mRemoteRecordCallbackList.register(recordCallback)
        }

        override fun setPreviewParams(width: Int, height: Int, format: Int) {
            mCameraPresenter.setPreviewParams(width, height, format)
        }

        /*override fun setSurfaceSize(width: Int, height: Int) {
            mCameraPresenter.setSurfaceSize(width, height)
        }*/

        override fun unregisterRecordStateCallback(recordCallback: IVideoRecordStateCallback) {
            mRemoteRecordCallbackList.unregister(recordCallback)
        }


        override fun stopRecord() {
            this@CameraService.mCameraPresenter.apply {
                if (isUIRecording()) {
                    this@CameraService.stopRecord()
                } else if (isRecording()) {
                    stopRecord(isLooper = isLoopRecording(), enableCheckPreOrDelay = false)
                }
            }

        }

        override fun isRecording(): Boolean {
            Timber.e("mIBinder.isRecording = ${this@CameraService.mCameraPresenter.isRecording()}")
            return this@CameraService.mCameraPresenter.isRecording()
        }

        override fun isUIRecording(): Boolean {
            return this@CameraService.mCameraPresenter.isUIRecording()
        }

        override fun isCapturing(): Boolean {
            return this@CameraService.mViewController?.isCapturing() == true
        }

        override fun isPreRecordEnabled(): Boolean {
            val preRecord = mCameraSettings.getRecordPre()
            Timber.w("preRecord = $preRecord")
            return preRecord > 0
        }

        override fun takePicture(needResult: Boolean, oneShot: Boolean) {
            /*if (oneShot) {
                this@CameraService.takePictureOneShot(needResult)
            } else {*/
                this@CameraService.takePicture(needResult, oneShot)
//            }
        }

        override fun takeBurstPicture(needResult: Boolean, burstCount: Int) {
            this@CameraService.takeBurstPicture(burstCount)
        }

        override fun showAt(x: Int, y: Int, width: Int, height: Int) {
            mFloatCameraManager.showAt(x, y, width, height)
            mViewController?.showRecordingStatus(true)
        }

        override fun focusOnPoint(x: Int, y: Int, cameraViewWidth: Int, cameraViewHeight: Int) {
            mCameraPresenter.focusOnPoint(x, y, cameraViewWidth, cameraViewHeight)
        }

        override fun release() {
            unbindAudioService()
        }

        override fun switchCamera() {
            mViewController?.switchCamera()
        }

        override fun getState(): Int {
            return mViewController?.getCameraState() ?: CameraCore.Status.RELEASE.ordinal
        }

        override fun getRecordStartTime(): Long {
            return mCameraPresenter.getStartRecordTime()
        }

        override fun getRecordStopTime(): Long {
            return mCameraPresenter.getStopRecordTime()
        }

    }

    override fun onBind(intent: Intent?): IBinder? {
        return mIBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Timber.d("onUnbind")
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("onCreate()")
        Observable.timer(200, TimeUnit.MILLISECONDS)
                .subscribe {
                    startForegroundNotification()
                }
        init()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Timber.e("onConfigurationChanged()")
        SoundPlayer.release()
        mViewController?.configurationChanged()
    }

    private fun startForegroundNotification() {
        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(CameraService::class.java.name, CameraService::class.java.name, NotificationManager.IMPORTANCE_HIGH)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
        startForeground(System.currentTimeMillis().toInt(), NotificationCompat.Builder(this, CameraService::class.java.name).setChannelId(channel.id).build())
        /*val notification = Notification.Builder(this, CameraService::class.java.name)
                .setVisibility(Notification.VISIBILITY_SECRET)
                .setSmallIcon(R.drawable.topbar_list_icon)
                .setContentText("Test")
                .build()
        notification.flags = notification.flags.or(Notification.FLAG_ONGOING_EVENT)
        manager.notify(System.currentTimeMillis().toInt(), notification)*/
    }

    inner class AudioServiceConnection: ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName?) {
            mAudioService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Timber.d("audio Service connected")
            mAudioService = IAudioRecordAIDL.Stub.asInterface(service)
            (mViewController as HViewController).setAudioService(mAudioService)
        }

    }

    private fun bindAudioService() = fixedThread {
//        bindService(Intent().setClassName(Values.PACKAGE_NAME_AUDIO, Values.CLASS_NAME_AUDIO_SERVICE), mAudioConnection, Context.BIND_AUTO_CREATE)
        bindService(Intent().setClass(this, RecordService::class.java), mAudioConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindAudioService() {
        Timber.d("unbindAudioService")
        unbindService(mAudioConnection)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        processAction(intent)
        return START_NOT_STICKY
    }

    private fun processAction(intent: Intent) {
        Timber.w("action = ${intent.action}")
        when (intent.action) {
            START_PREVIEW   -> {
                mViewController?.setRotation(intent.getIntExtra(ROTATION, 180))
                showWindow()
            }
            DISMISS_WINDOW -> {
                dismissWindow()
            }
            SHOW_WINDOW     -> {
                showWindow()
            }
            START_RECORD    -> {
                Flowable.just(Unit)
                        .observeOn(AndroidSchedulers.mainThread())
                        .map {
                            Timber.e("showWindow.currentThread = ${Thread.currentThread().name}")
                            dismissWindow()
                        }.delay(2, TimeUnit.SECONDS)
                        .subscribe {
                            Timber.e("startRecord.currentThread = ${Thread.currentThread().name}")
                            startRecord()
                        }
            }
            STOP_RECORD     -> {
                stopRecord()
            }
            TAKE_PICTURE    -> {
                takePicture(true, true)
            }
            BOOT_COMPLETE   -> {
                dismissWindow()
            }
            ACTION_ONE_SHOT -> {
                startOneShot()
                showWindow()
            }
        }
    }

    private var mPreRecord      = false
    private var mPreUIRecord    = false
    @Volatile
    private var mOneShot = false

    private fun startOneShot() {
        mOneShot = true
        mPreUIRecord = mCameraPresenter.isUIRecording()
        mPreRecord  = mCameraPresenter.isRecording()
        if (mPreRecord) {
            mCameraPresenter.stopRecord(isLooper = true, enableCheckPreOrDelay = false)
        }
    }

    private fun stopOneShot() {
        mOneShot = false
        if (mPreUIRecord) {
            startRecord()
        } else if (mPreRecord) {
            mCameraPresenter.checkPreRecordEnabled(true)
        }
    }

    private fun responseRecordResult(filePath: String) {
        Timber.e("WeChat RecordResult.[$filePath]")
        if (mVideoNeedResult.get()) {
            mVideoNeedResult.set(false)
            val intent = Intent().apply {
                action = MessageReceiver.ACTION_WECHAT_RECV
                putExtra(MessageReceiver.EXTRA_WECHAT_CMD, MessageReceiver.WECHAT_RESULT_TAKE_VID)
                putExtra(MessageReceiver.WECHAT_VID_PATH, filePath)
            }
            sendBroadcast(intent)
        }
    }

    private fun responseTakePictureResult(success: Boolean) {
        Timber.e("WeChat TakePictureResult[$success]. ${mPicNeedResult.get()}.")
        if (mPicNeedResult.get()) {
            mPicNeedResult.set(false)
            val intent = Intent().apply {
                action = MessageReceiver.ACTION_WECHAT_RECV
                putExtra(MessageReceiver.EXTRA_WECHAT_CMD, MessageReceiver.WECHAT_RESULT_TAKE_PIC)
                putExtra(MessageReceiver.WECHAT_PIC_PATH, if (success) mPictureFile!!.absolutePath else "")
            }
            sendBroadcast(intent)
        }
    }

    private fun takePicture(needResult: Boolean = false, oneShot: Boolean = false) {
        mViewController?.takePicture(needResult, oneShot)
    }

    private fun takePictureOneShot(needResult: Boolean) {
        mViewController?.takePicture(needResult)
    }

    private fun takeBurstPicture(burstCount: Int) {
        mCameraSettings.setPhotoMode(HCameraSettings.PhotoMode.CONTINUOUS.ordinal)
        mCameraSettings.setPhotoContinuousCount(burstCount)
        mViewController?.takePicture()
    }

    private fun stopRecord() {
        mViewController?.stopRecord()
    }

    private fun toggleRecord() {
        mViewController?.toggleRecord()
    }

    private fun startRecord(isImp: Boolean = false) {
        mViewController?.startRecord(isImp)
    }

    fun setSensorOrientation(orientation: Int) {
        mCameraPresenter.setSensorOrientation(orientation)
    }


    private fun zoomUp() {
        mCameraPresenter.zoomUp()
    }

    private fun zoomDown() {
        mCameraPresenter.zoomDown()
    }

    private fun getZoomMax(): Int {
        return mCameraPresenter.getZoomMax()
    }

    private fun setZoomLevel(level: Int) {
        mCameraPresenter.setZoomLevel(level)
    }

    private fun setFlashOn() {
        mCameraPresenter.setFlashOn()
    }

    private fun setFlashOff() {
        mCameraPresenter.setFlashOff()
    }

    /**
     * 注册预览界面
     * @param surface Any
     * @param width Int
     * @param height Int
     * @param rendCallback IFrameRenderCallback?
     * @param surfaceNeedRelease Boolean
     */
    fun registerPreviewSurface(surface: Any, width: Int, height: Int, rendCallback: IFrameRenderCallback?, surfaceNeedRelease: Boolean = false) {
        mCameraPresenter.registerPreviewSurface(surface, width, height, rendCallback != null, surfaceNeedRelease)
        mViewController?.showRecordingStatus(true)
        rendCallback?.apply {
            registerFrameRenderCallback(rendCallback, surface)
        }
    }

    /**
     * 注销预览界面
     * @param surface Any
     */
    fun unregisterPreviewSurface(surface: Any) {
        mCameraPresenter.unregisterPreviewSurface(surface)
        unregisterFrameRenderCallback(surface)
        mViewController?.showRecordingStatus(false)
    }

    /**
     * 注册渲染回调
     * @param renderCallback IFrameRenderCallback
     * @param surface Any
     */
    fun registerFrameRenderCallback(renderCallback: IFrameRenderCallback, surface: Any) {
        synchronized(mRemoteRenderCallbackList) {
            val id = System.identityHashCode(surface)
            Timber.tag(TAG.SURFACE_ENCODER).i("registerFrameRenderCallback. hashCode = ${mRenderMap[id]}")
            if (mRenderMap[id] == null) {
                mViewController?.setStreaming(true)
                mRenderMap[id] = renderCallback
                stopFrameRenderCallback()
                mRemoteRenderCallbackList.register(renderCallback)
                if (!mRenderListenerRegistered) {
                    mRenderListenerRegistered = true
                    mCameraPresenter.setOnFrameRenderListener(mFrameRenderListener)
                }
            }
        }
    }

    /**
     * 注销预览回调
     * @param surface Any
     */
    fun unregisterFrameRenderCallback(surface: Any) {
        synchronized(mRemoteRenderCallbackList) {
            val id = System.identityHashCode(surface)
            val containRender = mRenderMap.containsKey(id)
            Timber.tag(TAG.SURFACE_ENCODER).i("need unregisterFrameRenderCallback: $containRender")
            if (containRender) {
                stopFrameRenderCallback()
                mRemoteRenderCallbackList.unregister(mRenderMap[id])
                mRenderMap.remove(id)
                if (mRenderMap.isEmpty()) {
                    mRenderListenerRegistered = false
                    mCameraPresenter.setOnFrameRenderListener(null)
                }
            }
            if (mRenderMap.isEmpty()) {
                mViewController?.setStreaming(false)
            }
        }
    }

    private fun stopFrameRenderCallback() {
        if (mFrameRenderBeginBroadcast) {
            mRemoteRenderCallbackList.finishBroadcast()
            mFrameRenderBeginBroadcast = false
            mFrameRenderCount = 0
        }
    }

    private var mFrameRenderCount = 0

    @Volatile
    private var mFrameRenderBeginBroadcast = false

    inner class FrameRenderListener: SharedRender.OnFrameRenderListener {
        override fun onFrameSoon(id: Int) {
//            Timber.tag(TAG.SURFACE_ENCODER).v("onFrameSoon.[$id]")
            synchronized(mRemoteRenderCallbackList) {
                if (!mFrameRenderBeginBroadcast) {
                    mFrameRenderCount = mRemoteRenderCallbackList.beginBroadcast()
                    mFrameRenderBeginBroadcast = true
                }
                if (mFrameRenderCount > 0) {
                    val currentBack = mRenderMap[id]
                    for (i in 0 until mFrameRenderCount) {
                        val callback = mRemoteRenderCallbackList.getBroadcastItem(i)
                        if (currentBack == callback) {
                            callback.onFrameSoon()
                        }
                    }
                }
            }
        }

    }

    private var mPreviewCallbackCount = 0

    @Volatile
    private var mPreviewBeginBroadcast = false

    inner class PreviewCallback: ICameraManager.PreviewDataCallback {
        override fun onPreviewDataCallback(buffer: ByteArray, previewFormat: Int) {
//            Timber.tag(TAG.SURFACE_ENCODER).i("onPreviewDataCallback.")
            synchronized(mRemotePreviewCallbackList) {
                startPreviewBroadcast()
                if (mPreviewCallbackCount > 0) {
                    for (i in 0 until mPreviewCallbackCount) {
                        mRemotePreviewCallbackList.getBroadcastItem(i).onFrameCallback(buffer, previewFormat)
                    }
                }
            }
        }
    }

    private fun startPreviewBroadcast() {
        if (!mPreviewBeginBroadcast) {
            mPreviewCallbackCount = mRemotePreviewCallbackList.beginBroadcast()
            mPreviewBeginBroadcast = true
        }
    }

    private fun stopPreviewBroadcast() {
        if (mPreviewBeginBroadcast) {
            mRemotePreviewCallbackList.finishBroadcast()
            mPreviewCallbackCount = 0
            mPreviewBeginBroadcast = false
        }
    }

    private fun performRecord(isImp: Boolean = false) {
        mViewController?.performRecord(isImp)
    }

    private fun showWindow() {
        mViewController?.showAllView()
        mFloatCameraManager.showFloatWindow()
        /*mCameraPresenter.mSurfaceHolder?.apply {
            mCameraPresenter.registerPreviewSurface(this.surface)
        }*/
        mViewController?.showRecordingStatus(true)
    }

    private fun dismissWindow() {
        mViewController?.hideAllView()
        /*mCameraPresenter.mSurfaceHolder?.apply {
            mCameraPresenter.unregisterPreviewSurface(this.surface)
        }*/
        mViewController?.showRecordingStatus(false)
        mFloatCameraManager.dismissWindow()
        if (mOneShot) {
            stopOneShot()
        }
    }

    private fun showSetting() {
//        mFloatSettingManager.showFloatWindow()
    }

    private fun lockRecord() {
//        mCameraPresenter.lockRecord()
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEventMainThread(event: String) {
        Timber.e("onEventMainThread.event = $event")
        when (event) {
            MessageReceiver.EXTRA_DISMISS_WIN   -> dismissWindow()
            MessageReceiver.EXTRA_SHOW_WIN      -> showWindow()
            MessageReceiver.EXTRA_ONE_SHOT_FINISH      -> {
                dismissWindow()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEventBackground(event: String) {
        Timber.e("onEventBackground.event = $event")
        when (event) {
            MessageReceiver.EXTRA_LOCK_VIDEO    -> lockRecord()
            MessageReceiver.PICTURE_NEED_RESULT -> {
                Timber.e("WeChat onEventBackground.Picture.${mPicNeedResult.get()}")
                if (!mPicNeedResult.get()) {
                    mPicNeedResult.set(true)
                    takePicture(needResult = true)
                }
            }
            MessageReceiver.VIDEO_NEED_RESULT -> {
                Timber.e("WeChat onEventBackground.Video.${mVideoNeedResult.get()}")
                if (!mVideoNeedResult.get()) {
                    mVideoNeedResult.set(true)
                    if (FileUtil.checkExternalStorageMounted(this)) {
//                        mCameraPresenter.recordNeedResult {
//                            responseRecordResult(it)
//                        }
                    } else {
                        mVideoNeedResult.set(false)
                        responseRecordResult("")
                    }
                }
            }
            MessageReceiver.EXTRA_STOP_RECORD -> {
                stopRecord()
            }
            MessageReceiver.EXTRA_TAKE_PICTURE -> {
                takePicture()
            }
            MessageReceiver.EXTRA_START_RECORD -> {
                startRecord()
            }
        }
    }


    private fun openFileDir() {
        if (FileUtil.checkExternalStorageMounted(this)) {
            dismissWindow()
            ContextUtil.startOtherActivity(this, Values.PACKAGE_NAME_FILE_MANAGER, Values.CLASS_NAME_FILE_MANAGER)
//            FileUtil.openMtkFolder(this, CommonConst.getRootDir(this)!!)
        } else {
            TTSToast.showToast(getString(R.string.external_storage_unmounted), true)
        }
    }

    private fun init() {
//        initWakeLock()
        mLogReceiver.registerReceiver()
        val dagger = DaggerCameraComponent.builder()
                .hCameraModule(HCameraModule())
                .cameraModule(CameraModule(this)).build()
        dagger.inject(this)
        bindAudioService()
//        mFloatCameraManager.setSize(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        mViewController = HViewController(this, mCameraPresenter, mContainer, dagger)
        (mViewController as HViewController).setRemoteCameraCallbackList(mRemoteCameraCallbackList)
        (mViewController as HViewController).setRemoteRecordCallbackList(mRemoteRecordCallbackList)
        (mContainer as MainLayout).setOnKeyPressedListener(BackPressed(mFloatCameraManager))
        mFloatCameraManager.setOnWindowDismissListener(object : FloatWinManager.OnDismissListener {
            override fun onWindowDismiss() {
//                mCameraPresenter.showRecordingStatus(false)
            }

        })
        setRedLedOpen(false)
        initReceiver()
        initEventBus()
    }

    private var mWakeLock: PowerManager.WakeLock? = null

    private fun initWakeLock() = fixedThread {
        val manager = getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, packageName)
    }

    private fun releaseWakeLock() {
        mWakeLock?.release()
    }

    private fun initReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            addAction(Values.ACTION_RELEASE_CAMERA)
            addAction(Values.ACTION_RESUME_CAMERA)
            addAction(STOP_RECORD)
            addAction(ACTION_CAPTURE)
            addAction(ACTION_CAMERA)
            addAction(ACTION_RECORD)
            addAction(ACTION_START_RECORD)
            addAction(ACTION_STOP_RECORD)
            addAction(ACTION_IR_LED)
            addAction(ACTION_IMPORTANT_MARK)
            addAction(ACTION_MIC)
            addAction(Intent.ACTION_SHUTDOWN)
            addAction(ACTION_LOOP_SETTING)
//            addAction(Intent.ACTION_LOCALE_CHANGED)
        }
        registerReceiver(mReceiver, intentFilter)
    }

    private fun releaseReceiver() {
        unregisterReceiver(mReceiver)
    }

    private fun initEventBus() {
        EventBusUtils.registerEvent(MessageReceiver.ACTION_CAMERA, this)
    }

    private fun releaseEventBus() {
        EventBusUtils.unregisterEvent(MessageReceiver.ACTION_CAMERA, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.e("onDestroy()")
//        mCameraPresenter.releaseCamera()
//        mUnbinder.unbind()
//        releaseWakeLock()
        mLogReceiver.unregisterReceiver()
        releaseCallback()
        unbindAudioService()
        mViewController?.release()
        mFloatCameraManager.removeFloatWindow()
//        mFloatSettingManager.removeFloatWindow()
        releaseReceiver()
        releaseEventBus()
        stopForeground(Service.STOP_FOREGROUND_REMOVE)
    }

    private fun releaseCallback() {
        synchronized(mRemotePreviewCallbackList) {
            mRemotePreviewCallbackList.kill()
        }
        synchronized(mRemoteRecordCallbackList) {
            mRemoteRecordCallbackList.kill()
        }
        synchronized(mRemoteRenderCallbackList) {
            mRemoteRenderCallbackList.kill()
        }
        synchronized(mRemoteCameraCallbackList) {
            mRemoteCameraCallbackList.kill()
        }
    }

    inner class HomeReceiver: BroadcastReceiver() {

        private var mMicReceiveTime = 0L
        private var mVidReceiveTime = 0L

        override fun onReceive(context: Context, intent: Intent) {
            Timber.e("action = ${intent.action}")
            when (intent.action) {
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS -> {
                    if (intent.getStringExtra(SYSTEM_REASON) == SYSTEM_HOME_KEY) {
                        dismissWindow()
                    }
                }
                Values.ACTION_RELEASE_CAMERA -> {
                    mViewController?.apply {
                        mPreRecord = getCameraState().and(CameraCore.LOOP_RECORD) == CameraCore.LOOP_RECORD
                    }
                    mViewController?.releaseCamera()
                    mAudioService?.stopRecord()
                }
                Values.ACTION_RESUME_CAMERA -> {
                    mViewController?.let {
                        if (mPreRecord) {
                            mPreRecord = false
                            it.startRecord()
                        } else {
                            (it as HViewController).checkCameraOpened(HViewController.EVENT_NONE)
                        }
                    }

                }
                STOP_RECORD -> {
                    mCameraPresenter.stopRecord(mCameraPresenter.isLoopRecording(), enableCheckPreOrDelay = false)
                    mAudioService?.stopRecord()
                }
                ACTION_MIC  -> {
                    if (mCameraSettings.getRecordPre() > 0) {
                        VibrateUtil(this@CameraService).start()
                        mCameraPresenter.getRecordView().showMsg(R.string.record_pre_disable_first)
                        TTSToast.showToast(R.string.record_pre_disable_first)
                        return
                    }
                    mMicReceiveTime = SystemClock.elapsedRealtime()
                    if (mCameraPresenter.isRecording()) {
                        Timber.e("mMicReceiveTime = $mMicReceiveTime; mVidReceiveTime = $mVidReceiveTime")
                        if (abs(mMicReceiveTime - mCameraPresenter.getStartRecordTime()) <= 2000) {
                            return
                        }
                        if (mCameraPresenter.isUIRecording()) {
                            mViewController?.stopRecord(false)
                            Observable.just(Unit)
                                    .delay(1000, TimeUnit.MILLISECONDS)
                                    .subscribe {
                                        controlRecordAudio(false)
                                    }
                        } else if (mCameraPresenter.isRecording()) {
//                            mCameraPresenter.stopRecord(mCameraPresenter.isLoopRecording(), enableCheckPreOrDelay = false)
                            VibrateUtil(this@CameraService).start()
                            mCameraPresenter.getRecordView().showMsg(R.string.record_pre_disable_first)
                            TTSToast.showToast(R.string.record_pre_disable_first)
                        }

                    } else {
                        controlRecordAudio(false)
                    }

                }
                ACTION_RECORD   -> {
                    mVidReceiveTime = SystemClock.elapsedRealtime()

//                    performRecord(intent.getBooleanExtra(ZZX_STATE, false))
                    performRecord(false)

                    /*if (mAudioService?.isRecording == true) {
                        mAudioService?.apply {
                            if ((SystemClock.elapsedRealtime() - startTime) <= 2000) {
                                return
                            }
                        }
                        if (mAudioService?.stopRecord() == true) {
                            Observable.just(Unit)
                                    .delay(650, TimeUnit.MILLISECONDS)
                                    .subscribe {
                                        controlRecordVideo(intent.getBooleanExtra(ZZX_STATE, false))
                                    }
                        }
                    } else {
                        controlRecordVideo(intent.getBooleanExtra(ZZX_STATE, false))
                    }*/

                }
                ACTION_START_RECORD -> {
                    mVidReceiveTime = SystemClock.elapsedRealtime()
                    startRecord(false)

                }
                ACTION_STOP_RECORD  -> {
                    mVidReceiveTime = SystemClock.elapsedRealtime()
                    stopRecord()
                }
                ACTION_CAPTURE -> {
                    when (intent.getIntExtra(ZZX_STATE, SHUTTER)) {
                        SHUTTER -> {
                            takePicture()
                        }
                        LONG_PRESSED    -> {
                            mViewController?.apply {
                                (this as HViewController).switchInfrared()
                            }
                        }
                    }
                }
                Intent.ACTION_SHUTDOWN  -> {
                    if (mCameraPresenter.isUIRecording()) {
                        mViewController?.stopRecord(false)
                    } else if (mCameraPresenter.isRecording()) {
                        mCameraPresenter.stopRecord(mCameraPresenter.isLoopRecording(), enableCheckPreOrDelay = false)
                    }
                }
                ACTION_LOOP_SETTING -> {
                    mCameraSettings.apply {
                        val preLoop = getNeedLoop()
                        setNeedLoop(!preLoop)
                        TTSToast.showWarn(if (preLoop) R.string.loop_disable else R.string.loop_enable)
                    }
                }
                ACTION_CAMERA -> {
                    when (intent.getIntExtra(ACTION_EXTRA_INT, -1)) {
                        EXTRA_SHOW_WIN -> {
                            val delay = intent.getIntExtra(EXTRA_DELAY, 0)
                            Observable.timer(delay.toLong(), TimeUnit.MILLISECONDS)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe {
                                        showWindow()
                                    }
                        }
                    }
                }
                Intent.ACTION_LOCALE_CHANGED -> {
//                    val local = resources.configuration.locales
//                    Timber.w("locale = $local")
                }
                ACTION_IR_LED   -> {
                    mInfraredOpened = mInfraredOpened.not()
                    setRedLedOpen(mInfraredOpened)
                    SoundPlayer.getInstance().playSound(context, if (mInfraredOpened) R.raw.tts_nv_open else R.raw.tts_nv_close, 0, 1f)
                }
                ACTION_IMPORTANT_MARK   -> {
                    mVidReceiveTime = SystemClock.elapsedRealtime()
                    performRecord(true)
                }
            }

        }
    }

    private fun setRedLedOpen(isOpen: Boolean) {
        LedController.INSTANCE.controlIrLed(isOpen)
        mCameraPresenter.setColorEffect(if (isOpen) Camera.Parameters.EFFECT_MONO else Camera.Parameters.EFFECT_NONE)
    }

    private fun controlRecordAudio(isImp: Boolean) {
        Timber.w("controlRecordAudio.isImp = $isImp. mAudioService = $mAudioService")
        if (isImp) {
            mAudioService?.recordImpAudio()
        } else {
            mAudioService?.toggleRecord()
        }
    }

    private fun controlRecordVideo(isImp: Boolean) {
        if (XXPermissions.isGranted(this, Permission.RECORD_AUDIO)) {
            performRecordVideo(isImp)
        } else {
            XXPermissions.with(this@CameraService).permission(Permission.RECORD_AUDIO)
                .request { _, _ ->
                    performRecordVideo(isImp)
                }
        }
    }

    private fun performRecordVideo(isImp: Boolean) {
        if (isImp) {
//            mCameraPresenter.toggleVideoIsImp()
            startRecord(true)
        } else {
            toggleRecord()
        }
    }

    /**
     * 开启录音.
     */
    private fun controlAudioRecord(action: String) {
        Intent().apply {
            setClassName(Values.PACKAGE_NAME_AUDIO, Values.CLASS_NAME_AUDIO_SERVICE)
            this.action = action
//            startForegroundService(this)
//            startService(this)
        }
    }

    inner class BackPressed(var floatWinManager: FloatWinManager): MainLayout.OnKeyPressedListener {


        override fun onKeyPressed(event: KeyEvent?): Boolean {
            if (event!!.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
                dismissWindow()
                return true
            }
            return false
        }
    }

    companion object {
        const val START_PREVIEW     = "StartPreview"
        const val DISMISS_WINDOW    = "DismissWindow"
        const val SHOW_WINDOW       = "ShowWindow"
        const val START_RECORD      = "StartRecord"
        const val STOP_RECORD       = "StopRecord"
        const val TAKE_PICTURE      = "TakePicture"
        const val BOOT_COMPLETE     = "BootComplete"
        const val ROTATION  = "rotation"

        const val SYSTEM_REASON     = "reason"
        const val SYSTEM_HOME_KEY   = "homekey"

        //Take Picture Key sendBroadcast Action.
        const val ACTION_CAPTURE = "zzx_action_capture"
        const val LONG_PRESSED = 0
        const val LONG_PRESSED_UP = 1
        const val SHUTTER = 2

        const val ACTION_ONE_SHOT   = "zzx_action_one_shot"

        //Record Video Key sendBroadcast Action.
        const val ACTION_RECORD = "zzx_action_record_new"

        const val ACTION_STOP_RECORD    = "android.intent.poc.action.videorecord.stop"
        const val ACTION_START_RECORD   = "android.intent.poc.action.videorecord.start"

        const val ACTION_IR_LED = "android.intent.action.OPEN_IR_LED"

        const val ACTION_IMPORTANT_MARK = "android.intent.action.IMPORTANT_VIDEO_MARK"

        //Record Audio Key sendBroadcast Action.
        const val ACTION_MIC = "android.intent.action.voice.record"//zzx_action_mic

        const val ZZX_STATE = "zzx_state"

        const val ACTION_LOOP_SETTING = "com.zzx.loop"

        const val ACTION_CAMERA = "ActionCamera"
        const val ACTION_EXTRA_INT      = "zzxExtraInt"
        const val EXTRA_DELAY   = "extraDelay"
        const val EXTRA_SHOW_WIN        = 1

        const val ACTION_CAMERA_REFRESH_LIGHT = "CameraRefreshLight"
        const val REFRESH_LASER   = "0"
        const val REFRESH_FLASH = "1"
        const val REFRESH_INFRARED    = "2"
    }

}