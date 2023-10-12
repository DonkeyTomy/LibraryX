package com.zzx.camera.h9.presenter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.ImageFormat
import android.media.CamcorderProfile
import android.os.Environment
import android.os.SystemClock
import android.util.Size
import com.zzx.camera.R
import com.zzx.camera.data.Global
import com.zzx.camera.data.HCameraSettings
import com.zzx.camera.presenter.ACameraPresenter
import com.zzx.camera.values.CommonConst
import com.zzx.camera.view.IRecordView
import com.zzx.camera.view.preference.RecordSettingPreference
import com.zzx.media.camera.ICameraManager
import com.zzx.media.custom.view.camera.ISurfaceView
import com.zzx.media.recorder.IRecorder
import com.zzx.media.recorder.video.RecorderLooper
import com.zzx.media.recorder.video.VideoRecorder
import com.zzx.media.utils.FileNameUtils
import com.zzx.utils.MediaScanUtils
import com.zzx.utils.StorageListener
import com.zzx.utils.alarm.VibrateUtil
import com.zzx.utils.file.FileUtil
import com.zzx.utils.rxjava.FlowableUtil
import com.zzx.utils.rxjava.FlowableUtil.setMainThread
import com.zzx.utils.zzx.ZZXMiscUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**@author Tomy
 * Created by Tomy on 2018/10/4.
 */
class HCameraPresenter<surface, camera>(context: Context, mICameraManager: ICameraManager<surface, camera>,
                                        mCameraView: ISurfaceView<surface, camera>, mRecordView: IRecordView,
                                        storageListener: StorageListener): ACameraPresenter<surface, camera>(context, mICameraManager,
        mCameraView, mRecordView, storageListener) {

    private val mSetting = HCameraSettings(context)

    private val mReceiver by lazy {
        PreDelayChangeReceiver()
    }

    private val mIntentFilter by lazy {
        IntentFilter(RecordSettingPreference.ACTION_RECORD_PRE_NOTIFY)
                .apply {
                    addAction(RecordSettingPreference.ACTION_RECORD_PREFERENCE_NOTIFY)
                    addAction(Intent.ACTION_TIME_TICK)
                    addAction(Intent.ACTION_TIME_CHANGED)
                }
    }

    init {
        context.registerReceiver(mReceiver, mIntentFilter)
    }


    override fun initCameraParams() {
        Timber.w("initCameraParams.mPreSize = $mPreSize; [$mPreWidth x $mPreHeight]")
        if (mPreSize) {
            mICameraManager.setPreviewParams(mPreWidth, mPreHeight, mPreFormat)
        } else {
            mICameraManager.setPreviewParams(Global.DEFAULT_VIDEO_WIDTH, Global.DEFAULT_VIDEO_HEIGHT, mPreFormat)
        }
    }

    private var mPreUIRecord = false
    private var mPreRecord = false

    private var mPreWidth   = 1280
    private var mPreHeight  = 720
    private var mPreFormat  = if (mIsCamera1) ImageFormat.YV12 else ImageFormat.YUV_420_888

    private var mPreSize = true

    /**
     * @see startRecord
     * @param width Int
     * @param height Int
     * @param format Int
     */
    override fun setPreviewParams(width: Int, height: Int, format: Int) {
        Timber.d("preSize = ${mPreWidth}x$mPreHeight. preFormat = $mPreFormat")
        Timber.d("size = ${width}x$height. format = $format")
        if (width == mPreWidth && height == mPreHeight
                && format == mPreFormat) {
            return
        }
        mPreWidth   = width
        mPreHeight  = height
        mPreFormat  = format
        mPreSize = width != Global.DEFAULT_VIDEO_WIDTH
        if (mPreSize) {
            mPreRecord = isRecording()
            mPreUIRecord    = isUIRecording()
        }
        Timber.i("setPreviewParams.mPreUIRecord = $mPreUIRecord; mPreRecord = $mPreRecord")
        if (isRecording()) {
            stopRecord(enableCheckPreOrDelay = false)
            Observable.timer(1900, TimeUnit.MILLISECONDS)
                    .observeOn(Schedulers.newThread())
                    .subscribe {
                        Timber.i("mPreUIRecord = $mPreUIRecord; mPreRecord = $mPreRecord")
                        if (mPreUIRecord) {
                            startRecord()
                        } else if (width == Global.DEFAULT_VIDEO_WIDTH && mPreRecord) {
                            checkPreRecordEnabled(true)
                        }
                    }
        }
        stopPreview()
        mICameraManager.setPreviewParams(width, height, format)
        startPreview()
    }

    override fun setCaptureParams(width: Int, height: Int) {
        mICameraManager.setCaptureParams(width, height, ImageFormat.JPEG)
    }

    /**
     * 切换文件是否需要重要标记状态
     */
    fun toggleVideoIsImp(): Boolean {
        mNeedLock = !mNeedLock
        mRecordView.showImpIcon(mNeedLock)
        return mNeedLock
    }

    /**
     * 配置循环录像
     */
    private fun setupRecordLooper() {
        val index = mSetting.getVideoRatio()
        val highQuality = mSetting.getRecordHighQuality()
        val useHevc = mSetting.isUseHevc()
        Timber.e("highQuality = $highQuality; index = $index")
        when (index) {
            HCameraSettings.RECORD_RATIO_HIGH -> {
                mRecorderLooper?.setQuality(VideoRecorder.QUALITY_QHD, highQuality, useHevc)
            }
            HCameraSettings.RECORD_RATIO_1080 -> {
                mRecorderLooper?.setQuality(CamcorderProfile.QUALITY_1080P, highQuality, useHevc)
            }
            HCameraSettings.RECORD_RATIO_720 -> {
                mRecorderLooper?.setQuality(CamcorderProfile.QUALITY_720P, highQuality, useHevc)
            }
            HCameraSettings.RECORD_RATIO_480 -> {
                mRecorderLooper?.setQuality(CamcorderProfile.QUALITY_480P, highQuality, useHevc)
            }
        }
        val duration = mSetting.getRecordDuration()
        mRecorderLooper?.setRecordDuration(duration * 60 + 2)
    }

    /**
     * 开始录像.
     * @see stopRecord
     * @see setPreviewParams
     * @param isLooper Boolean 是否调用循环录像.与[stopRecord]必须对应,否则不会取消已启动的循环录像.
     * @param refreshUI 是否刷新UI.可用于只后台录像不刷新UI.
     */
    override fun startRecord(isLooper: Boolean, refreshUI: Boolean) {
        if (isUIRecording() || mRecorderLooper?.isRecordStartingOrStopping() == true) {
            Timber.i("startRecord cancel")
            return
        }
        mRecordStartTime = SystemClock.elapsedRealtime()
        setupRecordLooper()
        Flowable.just(Unit)
                .observeOn(Schedulers.newThread())
                .map {
                    return@map if (!mSetting.isUseExternalStorage()) {
                        if (mSetting.getNeedLoop() || FileUtil.getDirFreeSpaceByMB(Environment.getExternalStorageDirectory()) > 50) {
                            1
                        } else {
                            0
                        }
                    } else {
                        when (FileUtil.getExternalStorageState(mContext)) {
                            Environment.MEDIA_MOUNTED     -> {
                                if (mSetting.getNeedLoop() || FileUtil.getDirFreeSpaceByMB(
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
                    when (it) {
                        1   -> {
                            FlowableUtil.setMainThreadMapBackground<Unit>(
                                {
                                        if (refreshUI)
                                            mRecordView.startRecord()
                                    },
                                {
                                        ZZXMiscUtils.toggleLed(ZZXMiscUtils.LED_RED, true, mContext)
                                        mRecorderLooper?.apply {
                                            if (isRecording()) {
                                                mRecordView.enableCameraBtn()
                                            }
                                            setNeedLoopDelete(mSetting.getNeedLoop())
                                            if (isLooper) {
                                                startLooper()
                                            } else {
                                                startRecord()
                                            }
                                        }
                                    }
                            )
                        }
                        0   -> {
                            recordError(R.string.storage_not_enough)
                        }
                        -1  -> {
                            recordError(R.string.external_storage_unmounted)
                        }
                        -2  -> {
                            recordError(R.string.external_storage_unmountable)
                        }
                    }
                }
    }

    /**
     * 检查是否启动预录功能
     */
    override fun checkPreRecordEnabled(needStart: Boolean): Boolean {
        val recordPre = mSetting.getRecordPre()
        Timber.e("recordPre = $recordPre")
        return if (recordPre > 0) {
            if (needStart) {
                mRecordView.noticeRecording(true)
                mRecorderLooper?.setNeedLoopDelete(mSetting.getNeedLoop())
                mRecorderLooper?.startLooper(recordPre + 2, true)
            }
            true
        } else {
            false
        }
    }


    fun preRecordChanged() {
        FlowableUtil.setBackgroundThread {
            if (!checkPreRecordEnabled()) {
                if (!isUIRecording() && isLoopRecording()) {
                    mRecorderLooper?.stopLooper()
                    mRecordView.noticeRecording(false)
                }
            }
        }

    }

    /**
     * 检查是否启动延录功能
     */
    override fun checkDelayRecordEnabled(needStart: Boolean): Boolean {
        val recordDelay = mSetting.getRecordDelay()
        return if (recordDelay > 0) {
            if (needStart) {
                VibrateUtil.getInstance(mContext).vibrateOneShot()
                mRecorderLooper?.delayStop(recordDelay) {
                    setMainThread {
                        mRecordView.stopRecord()
//                        checkPreRecordEnabled()
                    }
                }
            }
            true
        } else
            false
    }

    /**
     * 停止录像.
     * @see startRecord
     * @param isLooper Boolean 是否调用循环录像.与[stopRecord]必须对应,否则不会取消已启动的录像.
     * @param enableCheckPreOrDelay 代表是否检测预录延录功能,某些情况下要禁止检测而直接停止录像,如切换到录音的情况下,否则会导致双方MIC冲突.
     */
    override fun stopRecord(isLooper: Boolean, enableCheckPreOrDelay: Boolean): Boolean {
//        if (enableCheckPreOrDelay && (!isRecording() || mRecorderLooper?.isDelayRecord() == true || abs(SystemClock.elapsedRealtime() - mRecordStartTime) <= 2000 || mRecorderLooper?.isRecordStartingOrStopping() == true)) {
        if (enableCheckPreOrDelay && mRecorderLooper?.isDelayRecord() != true && (!isRecording() || abs(SystemClock.elapsedRealtime() - mRecordStartTime) <= 2000 || mRecorderLooper?.isRecordStartingOrStopping() == true)) {
            Timber.d("isDelayRecord: ${mRecorderLooper?.isDelayRecord()}; isRecording: ${isRecording()}")
            return false
        }
        Timber.d("stopRecord. isLooper = $isLooper, enable = $enableCheckPreOrDelay")
        mRecordStopTime = SystemClock.elapsedRealtime()
        FlowableUtil.setMainThreadMapBackground<Unit>(
                {
                    if (!enableCheckPreOrDelay || mRecorderLooper?.isDelayRecord() == true || !checkDelayRecordEnabled(false))
                        mRecordView.stopRecord()
                },
                {
                    mRecorderLooper?.apply {
//                        ZZXMiscUtils.toggleLed(ZZXMiscUtils.LED_GREEN, context = mContext, oneShot = true)
                        if (!enableCheckPreOrDelay || isDelayRecord()) {
                            when {
                                isDelayRecord() -> {
                                    mNeedCheckPreMode = enableCheckPreOrDelay
                                    stopLooper()
//                                    stopRecord()
                                }
                                isLooper -> stopLooper()
                                else -> stopRecord()
                            }
                            mRecordView.noticeRecording(false)
                        } else if (checkDelayRecordEnabled()) {

                        } else {
                            if (isLooper) {
                                mNeedCheckPreMode = true
                                stopLooper()
                            } else {
                                stopRecord()
                                checkPreRecordEnabled()
                            }
                        }
                    }

                }

        )
        return true
    }

    /**
     * 切换录像状态: stop/start
     */
    override fun toggleRecord() {
        if (isUIRecording()) {
            stopRecord()
        } else {
            startRecord()
        }
    }

    /**
     * 锁定当前录像文件,若当前没录像则会先启动录像.
     */
    override fun lockRecord() {
    }

    /**
     * 释放
     */
    override fun releaseCamera() {
        if (isRecording()) {
            stopRecord(enableCheckPreOrDelay = false)
            mNeedReleaseCamera = true
            /*Observable.just(Unit)
                    .delay(500, TimeUnit.MILLISECONDS)
                    .subscribe {
                        mICameraManager.releaseCamera()
                    }*/
        } else {
            mICameraManager.releaseCamera()
        }
    }

    override fun release() {
        releaseCamera()
        mStorageListener.release()
        mContext.unregisterReceiver(mReceiver)
    }

    /**
     * 显示录像状态.8.0后悬浮窗中AnimationDrawable显示会有问题,需要放置在显示悬浮窗的时候去调用.
     * @param show Boolean
     */
    override fun showRecordingStatus(show: Boolean) {
        mRecordView.isShow(show)
    }

    override fun recordError(errorMsg: Int) {
        mRecordView.recordError(errorMsg, true)
    }

    override fun recordError(errorMsg: String) {
        mRecordView.recordError(errorMsg, true)
    }

    override fun recordFinished(file: File?): File? {
        Timber.e("recordFinished: needLock = $mNeedLock; file = $file")
        if (mNeedReleaseCamera) {
            mNeedReleaseCamera = false
            mICameraManager.releaseCamera()
        }
        file?.apply {
            val result = if (mNeedLock) {
//                mNeedLock = false
                FileNameUtils.tmpFile2ImpVideo(this)
            } else {
                FileNameUtils.tmpFile2Video(this)
            }
            Timber.e("result = ${result.name}")
            try {
                if (exists()) {
                    MediaScanUtils(mContext, result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            showThumb(result)
            return result
        }
        return null
    }

    var mFile: File? = null

    fun showThumb(file: File?) {
        mFile = file
        mRecordView.showThumb(file)
    }

    override fun zoomUp(level: Int) {
        mICameraManager.zoomUp(level)
    }

    override fun zoomDown(level: Int) {
        mICameraManager.zoomDown(level)
    }

    override fun getZoomMax(): Int {
        return mICameraManager.getZoomMax()
    }

    override fun setZoomLevel(level: Int) {
        mICameraManager.setZoomLevel(level)
    }

    override fun setFlashOff() {
        mICameraManager.setFlashOff()
    }

    override fun getSupportPictureSizeList(): Array<Size> {
        return mICameraManager.getSupportCaptureSizeList()
    }

    override fun setFlashOn() {
        mICameraManager.setFlashOn()
    }

    override fun setColorEffect(colorEffect: String) {
        mICameraManager.setColorEffect(colorEffect)
    }

    override fun getColorEffect(): String {
        return mICameraManager.getColorEffect()
    }

    override fun isSurfaceCreated(): Boolean {
        return mSharedRender.isRenderBusy()
//        return mSurfaceCreated.get()
    }

    inner class PreDelayChangeReceiver: BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            Timber.d("storage action = ${intent.action}")
            when (intent.action) {
                Intent.ACTION_TIME_TICK, Intent.ACTION_TIME_CHANGED -> {
                    if (FileUtil.checkExternalStorageMounted(mContext)) {
                        mRecorderLooper?.setDirPath(CommonConst.getVideoDir(mContext)!!.absolutePath)
                    }
                }
                RecordSettingPreference.ACTION_RECORD_PRE_NOTIFY -> {
                    preRecordChanged()

                }
            }
        }

    }

}