package com.zzx.camera.service

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.*
import android.content.res.Configuration
import android.hardware.Camera
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.SystemClock
import android.view.KeyEvent
import android.view.SurfaceHolder
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.tomy.lib.ui.manager.FloatWinManager
import com.tomy.lib.ui.view.layout.MainLayout
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import com.zzx.camera.ICameraServiceAIDL
import com.zzx.camera.R
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
import com.zzx.recorder.audio.IRecordAIDL
import com.zzx.utils.TTSToast
import com.zzx.utils.alarm.SoundPlayer
import com.zzx.utils.alarm.VibrateUtil
import com.zzx.utils.context.ContextUtil
import com.zzx.utils.event.EventBusUtils
import com.zzx.utils.file.FileUtil
import com.zzx.utils.rxjava.fixedThread
import com.zzx.utils.zzx.SystemInfo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.io.File
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

    /** 拍照后是否需要将路径返回 **/
    private val mPicNeedResult = AtomicBoolean(false)

    /** 录像后是否需要将路径返回 **/
    private val mVideoNeedResult = AtomicBoolean(false)

    private val mCameraSettings by lazy {
        HCameraSettings(this)
    }

    private val mReceiver by lazy {
        HomeReceiver()
    }

    private var mAudioService: IRecordAIDL? = null

    private val mAudioConnection by lazy {
        AudioServiceConnection()
    }

    private val mIBinder = object : ICameraServiceAIDL.Stub() {

        override fun startRecord(needResult: Boolean, oneShot: Boolean) {
            this@CameraService.startRecord()
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

        override fun isPreRecordEnabled(): Boolean {
            val preRecord = mCameraSettings.getRecordPre()
            Timber.w("preRecord = $preRecord")
            return preRecord > 0
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

    @SuppressLint("CheckResult")
    override fun onCreate() {
        super.onCreate()
        Observable.timer(1000, TimeUnit.MILLISECONDS)
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
        val channel = NotificationChannel(CameraService::class.java.name, CameraService::class.java.name, NotificationManager.IMPORTANCE_HIGH)
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
            mAudioService = IRecordAIDL.Stub.asInterface(service)
            (mViewController as HViewController).setAudioService(mAudioService)
        }

    }

    private fun bindAudioService() = fixedThread {
        bindService(Intent().setClassName(Values.PACKAGE_NAME_AUDIO, Values.CLASS_NAME_AUDIO_SERVICE), mAudioConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindAudioService() {
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
                takePicture()
            }
            BOOT_COMPLETE   -> {
                dismissWindow()
            }
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

    private fun takePicture() {
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

    private fun performRecord(isImp: Boolean = false) {
        mViewController?.performRecord(isImp)
    }

    private fun showWindow() {
        mFloatCameraManager.showFloatWindow()
        mViewController?.showRecordingStatus(true)
    }

    private fun dismissWindow() {
        mViewController?.showRecordingStatus(false)
        mFloatCameraManager.dismissWindow()
    }

    private fun showSetting() {
//        mFloatSettingManager.showFloatWindow()
    }

    private fun lockRecord() {
//        mCameraPresenter.lockRecord()
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEventMainThread(event: String) {
        Timber.e("onEventBackground.event = $event")
        when (event) {
            MessageReceiver.EXTRA_DISMISS_WIN   -> dismissWindow()
            MessageReceiver.EXTRA_SHOW_WIN      -> showWindow()
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
                    takePicture()
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
        val dagger = DaggerCameraComponent.builder()
                .hCameraModule(HCameraModule())
                .cameraModule(CameraModule(this)).build()
        dagger.inject(this)
        bindAudioService()
        mFloatCameraManager.setSize(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        mViewController = HViewController(this, mCameraPresenter, mContainer, dagger)
        (mContainer as MainLayout).setOnKeyPressedListener(BackPressed(mFloatCameraManager))
        mFloatCameraManager.setOnWindowDismissListener(object : FloatWinManager.OnDismissListener {
            override fun onWindowDismiss() {
//                mCameraPresenter.showRecordingStatus(false)
            }

        })

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
            addAction(STOP_RECORD)
            addAction(ACTION_CAPTURE)
            addAction(ACTION_CAMERA)
            addAction(ACTION_RECORD)
            addAction(ACTION_MIC)
            addAction(Intent.ACTION_SHUTDOWN)
            addAction(ACTION_LOOP_SETTING)
            if (SystemInfo.getDeviceModel().contains("k94", true)) {
                addAction(ACTION_SONIM_SOS_UP)
            }
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
        Timber.e("CameraService.onDestroy()")
//        mCameraPresenter.releaseCamera()
//        mUnbinder.unbind()
//        releaseWakeLock()
        unbindAudioService()
        mViewController?.release()
        mFloatCameraManager.removeFloatWindow()
//        mFloatSettingManager.removeFloatWindow()
        releaseReceiver()
        releaseEventBus()
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
                    mViewController?.releaseCamera()
                    mAudioService?.stopRecord()
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
                                    .delay(500, TimeUnit.MILLISECONDS)
                                    .subscribe {
                                        controlRecordAudio(intent.getBooleanExtra(ZZX_STATE, false))
                                    }
                        } else if (mCameraPresenter.isRecording()) {
//                            mCameraPresenter.stopRecord(mCameraPresenter.isLoopRecording(), enableCheckPreOrDelay = false)
                            VibrateUtil(this@CameraService).start()
                            mCameraPresenter.getRecordView().showMsg(R.string.record_pre_disable_first)
                            TTSToast.showToast(R.string.record_pre_disable_first)
                        }

                    } else {
                        controlRecordAudio(intent.getBooleanExtra(ZZX_STATE, false))
                    }

                }
                ACTION_SONIM_SOS_UP -> {
                    val longPress = intent.getBooleanExtra(ZZX_STATE, false)
                    if (!longPress) {
                        mVidReceiveTime = SystemClock.elapsedRealtime()
                        performRecord(true)
                    }
                }
                ACTION_RECORD   -> {
                    mVidReceiveTime = SystemClock.elapsedRealtime()

                    performRecord(intent.getBooleanExtra(ZZX_STATE, false))

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
                ACTION_CAPTURE -> {
                    takePicture()
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
                        TTSToast.showToast(if (preLoop) R.string.loop_disable else R.string.loop_enable)
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
                    val local = resources.configuration.locales[0]
                    Timber.w("locale = $local")
                }
            }

        }
    }

    private fun controlRecordAudio(isImp: Boolean) {
        Timber.w("controlRecordAudio.isImp = $isImp")
        if (isImp) {
            mAudioService?.recordImpVideo()
        } else {
            mAudioService?.toggleRecord()
        }
    }

    private fun controlRecordVideo(isImp: Boolean) {
        if (AndPermission.hasPermissions(this, Permission.RECORD_AUDIO)) {
            performRecordVideo(isImp)
        } else {
            AndPermission.with(this@CameraService).runtime().permission(Permission.RECORD_AUDIO)
                    .onGranted {
                        it.forEach {
                            Timber.e("onGranted.action = $it")
                        }
                        performRecordVideo(isImp)
                    }
                    .start()
        }
    }

    private fun performRecordVideo(isImp: Boolean) {
        if (isImp) {
//            mCameraPresenter.toggleVideoIsImp()
            startRecord(isImp)
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
            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                startForegroundService(this)
            } else {
                startService(this)
            }
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

        //Record Video Key sendBroadcast Action.
        const val ACTION_RECORD = "zzx_action_record"

        //Record Audio Key sendBroadcast Action.
        const val ACTION_MIC = "zzx_action_mic"

        const val ZZX_STATE = "zzx_state"

        const val ACTION_LOOP_SETTING = "com.zzx.loop"

        const val ACTION_SONIM_SOS_UP = "com.sonim.intent.action.SOS_KEY_UP"

        const val ACTION_CAMERA = "ActionCamera"
        const val ACTION_EXTRA_INT      = "zzxExtraInt"
        const val EXTRA_DELAY   = "extraDelay"
        const val EXTRA_SHOW_WIN        = 1
    }

}