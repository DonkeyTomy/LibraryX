package com.zzx.camera.presenter

import android.content.Context
import android.graphics.ImageFormat
import android.util.Size
import com.zzx.camera.R
import com.zzx.camera.values.CommonConst
import com.zzx.camera.view.IRecordView
import com.zzx.media.camera.ICameraManager
import com.zzx.media.custom.view.camera.ISurfaceView
import com.zzx.media.recorder.IRecorder
import com.zzx.media.recorder.video.RecorderLooper
import com.zzx.utils.StorageListener
import com.zzx.utils.TTSToast
import com.zzx.utils.file.FileLocker
import com.zzx.utils.file.FileUtil
import com.zzx.utils.file.IFileLocker
import com.zzx.utils.rxjava.FlowableUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit

/**@author Tomy
 * Created by Tomy on 2018/5/31.
 */
class CameraPresenter<surface, camera>(mContext: Context,mICameraManager: ICameraManager<surface, camera>,
                                       mCameraView: ISurfaceView<surface, camera>, mRecordView: IRecordView,
                                       mStorageListener: StorageListener, var mSettingPresenter: SettingPresenter):
        ACameraPresenter<surface, camera>(mContext, mICameraManager,
    mCameraView, mRecordView, mStorageListener) {


    private var mLastDuration   = 0
    private var mCurrentDuration= 0
    private var mLastRecordVoice    = false
    private var mCurrentRecordVoice = false

    private var mFileLocker = FileLocker(null).apply {
        setLockListener(LockListener())
    }

    init {
        mCameraView.setPreviewSize(864, 480)
        initRecordLooper()
        mSettingPresenter.setSettingChangeListener(SettingChanged())
    }

    override fun releaseCamera() {
        stopRecord()
        mRecorderLooper?.apply {
            release()
        }
    }

    override fun release() {
        releaseCamera()
        mStorageListener.release()
    }

    override fun showRecordingStatus(show: Boolean) {
        mRecordView.isShow(show)
    }

    private fun initRecordLooper() {
        mRecorderLooper = RecorderLooper<surface, camera>(mContext, getRecordFlag()).apply {
            setCameraManager(mICameraManager)
            mLastDuration       = mSettingPresenter.getRecordDuration()
            mCurrentDuration    = mLastDuration
            mLastRecordVoice    = mSettingPresenter.getVoice()
            mCurrentRecordVoice  = mLastRecordVoice
            setRecordDuration(mLastDuration)
            setFlag(getRecordFlag())
            setRecordCallback(RecordCallback())
        }
    }

    override fun initCameraParams() {
        setPreviewParams(720, 1280, if (mIsCamera1) ImageFormat.YV12 else ImageFormat.YUV_420_888)
        setCaptureParams(720, 1280)
    }

    override fun setPreviewParams(width: Int, height: Int, format: Int) {
        mICameraManager.setPreviewParams(width, height, format)
    }

    override fun setCaptureParams(width: Int, height: Int) {
        mICameraManager.setCaptureParams(width, height, ImageFormat.JPEG)
    }


    fun getRecordFlag(): Int {
        return if (mSettingPresenter.getVoice())
            IRecorder.VIDEO
        else
            IRecorder.VIDEO_MUTE
    }


    override fun setRotation(rotation: Int) {
        mRotation = 1
        mCameraView.setRotation(mRotation)
        mRecorderLooper?.setRotation(mRotation)
    }

    override fun getCameraCount(): Int {
        return mICameraManager.getCameraCount()
    }

    /**
     * 录像需要返回路径.
     * */
    fun recordNeedResult(result: (path: String) -> Unit) {
        Timber.e("recordNeedResult")
        if (mRecorderLooper!!.isRecording()) {
            mPreRecording.set(true)
            stopRecord()
        } else {
            mPreRecording.set(false)
        }
        Observable.just(Unit)
                .delay(350, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    Timber.e("recordNeedResult1")
                    mRecordView.startRecord()
                }.observeOn(Schedulers.io())
                .map {
                    mRecorderLooper!!.setDirPath(CommonConst.getVideoDir(mContext)!!.absolutePath)
                    mRecorderLooper!!.startRecord()
                }.delay(10, TimeUnit.SECONDS)
                .map {
                    Timber.e("recordNeedResult2")
                    mRecorderLooper!!.stopRecord()
                    result(mRecorderLooper!!.getOutputFile()!!.absolutePath)
                }.observeOn(AndroidSchedulers.mainThread())
                .map {
                    mRecordView.stopRecord()
                }.observeOn(Schedulers.io())
                .subscribe {
                    if (mPreRecording.get()) {
                        mPreRecording.set(false)
                        startRecord()
                    }
                }

    }


    override fun startRecord(isLooper: Boolean, refreshUI: Boolean) {
        if (mLocking.get()) {
            TTSToast.showToast(R.string.locking, true, show = false)
            return
        }
        Flowable.just(Unit)
                .observeOn(Schedulers.newThread())
                .map {
                    return@map FileUtil.checkExternalStorageMounted(mContext)
                }.observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it) {
                        FlowableUtil.setMainThreadMapBackground<Unit>(
                                 {
                                    mRecordView.startRecord()
                                },  {
                                    mRecorderLooper!!.setDirPath(CommonConst.getVideoDir(mContext)!!.absolutePath)
                                    mRecorderLooper?.apply {
                                        if (isLooper) {
                                            startLooper()
                                        } else {
                                            startRecord()
                                        }
                                    }
                                }
                        )
                    } else {
                        mRecordView.recordError(R.string.external_storage_unmounted)
                    }
                }

    }

    /**
     * 检查是否启动预录功能
     */
    override fun checkPreRecordEnabled(needStart: Boolean): Boolean {
        return false
    }

    /**
     * 检查是否启动延录功能
     */
    override fun checkDelayRecordEnabled(needStart: Boolean): Boolean {
        return false
    }

    override fun toggleRecord() {
        if (mRecorderLooper!!.isRecording()) {
            stopRecord()
        } else {
            startRecord()
        }
    }

    override fun stopRecord(isLooper: Boolean, enableCheckPreOrDelay: Boolean): Boolean {
        if (mLocking.get()) {
            TTSToast.showToast(R.string.locking, true, show = false)
            return false
        }
        Timber.e("stopRecord")
        FlowableUtil.setMainThreadMapBackground<Unit>(
                 {
                    mRecordView.stopRecord()
                },  {
                    mRecorderLooper?.apply {
                        if (isLooper) {
                            stopLooper()
                        } else {
                            stopRecord()
                        }
                    }
                }
        )
        return true
    }

    override fun recordError(errorMsg: Int) {
        recordError(mContext.getString(errorMsg))
    }

    override fun recordError(errorMsg: String) {
        FlowableUtil.setMainThreadMapBackground(
             {
                Timber.e("mRecordView.stopRecord()")
                forceLockFinish()
                mRecordView.stopRecord(false)
                mRecordView.recordError(errorMsg)
            },  {
                Timber.e("mRecorderLooper!!.stopLooper()")
                mRecorderLooper!!.stopLooper()
            }
        )
        Timber.e("recordError")
    }

    override fun recordFinished(file: File?): File? {
        return null
    }

    private var mLockDisposable: Disposable? = null

    private fun forceLockFinish() {
        if (!mLocking.get()) {
            return
        }
        mLockDisposable?.dispose()
        mLockDisposable = null
        mLocking.set(false)
        mRecordView.lockRecordFinished(false, false)
    }

    override fun lockRecord() {
        if (mLocking.get()) {
            TTSToast.showToast(R.string.locking, true, show = false)
            return
        }
        Observable.just(Unit)
                .observeOn(Schedulers.newThread())
                .map {
                    return@map FileUtil.checkExternalStorageMounted(mContext)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    Timber.e("lockRecord().storageMounted = $it")
                    if (it) {
                        if (!mRecorderLooper!!.isRecording()) {
                            startRecord()
                        }
                        mLocking.set(true)
                        mRecordView.lockRecordStarting()
                    } else {
                        mLockDisposable?.dispose()
                        TTSToast.showToast(R.string.external_storage_unmounted, true, show = false)
                    }
                }.observeOn(Schedulers.newThread())
                .delay(300, TimeUnit.MILLISECONDS)
                .map {
                    Timber.e("lockRecord currentThread = ${Thread.currentThread().name}")
                    mRecorderLooper!!.cancelLooperTimer()
                }
                .delay(180, TimeUnit.SECONDS)
                .map {
                    val mOutputFile = mRecorderLooper!!.getOutputFile()
                    Timber.e("lockFile: ${mOutputFile?.absolutePath}. currentThread = ${Thread.currentThread().name}")
                    mRecorderLooper!!.recordSection()
                    mFileLocker.setLockDir(CommonConst.getLockDir(mContext, mOutputFile!!.name))
                    mFileLocker.startLockFile(mOutputFile)
                    mLocking.set(false)
                }
                .subscribe({}, {}, {
                    Timber.e("onSubscribe")
//                    mLockDisposable = it
                })
    }

    /**
     * 若使用Camera1，则某些情况下需要手动重新预览。
     * */
    private fun checkNeedRestartPreview() {
        if (mIsCamera1)
            mICameraManager.startPreview()
    }

    /*inner class RecordCallback: IRecorder.IRecordCallback {

        override fun onRecorderPrepared() {
        }

        override fun onRecorderConfigureFailed() {
            recordError(R.string.record_configure_error)
        }

        override fun onRecordError() {
            recordError()
        }

        override fun onRecorderFinished(file: File?) {
            checkNeedRestartPreview()
        }

    }*/

    inner class LockListener: IFileLocker.FileLockListener {

        override fun onLockStart() {

        }

        override fun onLockFinished() {
            mRecordView.lockRecordFinished(true)
        }

        override fun onLockFailed() {
            mRecordView.lockRecordFinished(false)
        }

    }

    fun checkSettingChanged() {
        if (mCurrentDuration != mLastDuration || mCurrentRecordVoice != mLastRecordVoice) {
            mRecorderLooper!!.setRecordDuration(mCurrentDuration)
            mRecorderLooper!!.setFlag(if (mCurrentRecordVoice) IRecorder.VIDEO else IRecorder.VIDEO_MUTE)
            mLastDuration       = mCurrentDuration
            mLastRecordVoice    = mCurrentRecordVoice
            Observable.just(1)
                    .map {
                        stopRecord()
                    }.delay(1, TimeUnit.SECONDS)
                    .subscribe {
                        startRecord()
                    }
        }
    }

    inner class StorageCallback: StorageListener.StorageCallback {
        override fun onAvailablePercentChanged(percent: Int) {

        }

        override fun onExternalStorageChanged(exist: Boolean, mounted: Boolean) {
            if(exist)
                startRecord()
        }

    }

    inner class SettingChanged: SettingPresenter.SettingChangeListener {
        override fun onSettingDurationChanged(duration: Int) {
            mCurrentDuration    = duration
        }

        override fun onCollideLevelChanged(collide: Int) {

        }

        override fun onVoiceChanged(needVoice: Boolean) {
            mCurrentRecordVoice = needVoice
        }

    }

    override fun zoomUp(level: Int) {
    }

    override fun zoomDown(level: Int) {
    }

    override fun getZoomMax(): Int {
        return mICameraManager.getZoomMax()
    }

    override fun setZoomLevel(level: Int) {
    }

    override fun setFlashOff() {

    }

    override fun getSupportPictureSizeList(): Array<Size> {
        return emptyArray()
    }

    override fun setFlashOn() {

    }

    override fun isSurfaceCreated(): Boolean {
        return mSurfaceCreated.get()
    }


    override fun getColorEffect(): String {
        TODO("Not yet implemented")
    }

    override fun setColorEffect(colorEffect: String) {

    }

    inner class SurfaceListener: ISurfaceView.StateCallback<surface> {

        override fun onSurfaceDestroyed(surface: surface?) {
            Timber.e("${CommonConst.TAG_RECORD_FLOW} onSurfaceDestroyed")
            mICameraManager.closeCamera()
        }

        override fun onSurfaceCreate(surface: surface?) {
            Timber.e("${CommonConst.TAG_RECORD_FLOW} onSurfaceCreate")
//            mSurface = surface
            mICameraManager.openBackCamera()
        }

        override fun onSurfaceSizeChange(surface: surface?, width: Int, height: Int) {
        }
    }


    inner class CameraStateCallback: ICameraManager.CameraStateCallback<camera> {

        override fun onCameraOpening() {
        }

        override fun onCameraOpenSuccess(camera: camera, id: Int) {
            Timber.e("${CommonConst.TAG_RECORD_FLOW} onCameraOpenSuccess")
            if (mIsCamera1) {
                mRecorderLooper?.setCamera(camera)
            }
            initCameraParams()
            startPreview()
        }

        override fun onCameraOpenFailed(errorCode: Int) {
        }

        override fun onCameraClosing() {
        }

        override fun onCameraClosed() {
        }

        override fun onCameraErrorClose(errorCode: Int) {
        }

        override fun onCameraPreviewSuccess() {
        }

        override fun onCameraPreviewStop() {
        }
    }

}