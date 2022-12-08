package com.zzx.camera.h9.addition

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import com.zzx.camera.R
import com.zzx.camera.data.HCameraSettings
import com.zzx.camera.h9.addition.ICaptureAddition.Companion.ACTION_CAPTURE_RESULT
import com.zzx.camera.h9.addition.ICaptureAddition.Companion.RESULT_PIC_PATH
import com.zzx.camera.presenter.ICameraPresenter
import com.zzx.camera.values.CommonConst
import com.zzx.media.camera.ICameraManager
import com.zzx.media.platform.FileSaver
import com.zzx.media.platform.IFileSaver
import com.zzx.media.recorder.IRecorder
import com.zzx.media.utils.FileNameUtils
import com.zzx.utils.alarm.SoundPlayer
import com.zzx.utils.alarm.VibrateUtil
import com.zzx.utils.file.FileUtil
import com.zzx.utils.rxjava.FlowableUtil
import com.zzx.utils.rxjava.singleThread
import com.zzx.utils.zzx.DeviceUtils
import com.zzx.utils.zzx.ZZXMiscUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**@author Tomy
 * Created by Tomy on 2018/10/22.
 */
class CaptureAddition(var mContext: Context, var mBtnCap: ImageView, var mSetting: HCameraSettings, var mCameraPresenter: ICameraPresenter,
                      var mCaptureCallback: ICaptureAddition.ICaptureCallback?, var mIvTimer: ImageView? = null): ICaptureAddition {

    private var mPictureFile: File? = null
    private var mDisposable: Disposable? = null
    private var mTimerDisposable: Disposable? = null
    private var mFileSaver: IFileSaver? = null

    private var mIntervalMode = AtomicBoolean(false)

    private var mIntervalCount = 0

    private var mDelayMode = false

    @Volatile
    private var mNeedResult = false

    private val mCapturing = AtomicBoolean(false)

    private val mUserCapturing = AtomicBoolean(false)

    private val mCaptureRation by lazy {
        mContext.resources.getStringArray(R.array.array_ratio_capture_txt)
    }

    private val mTimerAnimation by lazy {
        (AnimatorInflater.loadAnimator(mContext, R.animator.timer) as AnimatorSet).apply {
            setTarget(mIvTimer)
        }
    }

    init {
        mCameraPresenter.setPictureCallback(PictureCallback())
        setFileSaver(FileSaver(mContext))
    }

    fun setFileSaver(fileSaver: IFileSaver) {
        mFileSaver = fileSaver
    }

    override fun takePicture(needResult: Boolean) {
        Timber.w("mCapturing = ${mCapturing.get()}; isRecordStartingOrStopping = ${mCameraPresenter.isRecordStartingOrStopping()}\nmInIntervalMode = $mIntervalMode; mDelayMode = $mDelayMode")
        if (mCapturing.get()) {
            mCaptureCallback?.onCaptureError(ICameraManager.PictureCallback.ERROR_CODE_CAPTURING)
            return
        }
        if (mCameraPresenter.isRecordStartingOrStopping()) {
            mCaptureCallback?.onCaptureError(ICameraManager.PictureCallback.ERROR_CODE_START_STOP_RECORD)
            return
        }
        if (isIntervalOrDelayMode()) {
            clearPictureMode()
            return
        }
        mCaptureCallback?.onUserCaptureStart()
        getCaptureRatio()
        if (mCameraPresenter.isRecording()) {
            takeOneShot(needResult)
            return
        }
        mUserCapturing.set(true)
        mCameraPresenter.getRecordView().manualCaptureStart()
        when (mSetting.getPhotoMode()) {
            HCameraSettings.PhotoMode.CONTINUOUS.ordinal -> {
                takeBurstPicture(needResult)
            }
            HCameraSettings.PhotoMode.INTERVAL.ordinal  -> {
                takeIntervalPicture(needResult)
            }
            HCameraSettings.PhotoMode.TIMER.ordinal -> {
                takeDelayPicture(needResult)
            }
            else -> {
                takeOneShot(needResult)
            }
        }
    }

    private fun controlLed() {
        try {
            if (ZZXMiscUtils.isLedEnabled(mContext)) {
                Observable.just(Unit)
                        .observeOn(Schedulers.io())
                        .map {
                            val preState = ZZXMiscUtils.read(ZZXMiscUtils.RGB_LED) ?: ""
                            Timber.e("preState = $preState")
                            ZZXMiscUtils.toggleLed(ZZXMiscUtils.LED_RED, false, mContext, true)
                            return@map preState
                        }
                        .delay(500, TimeUnit.MILLISECONDS)
                        .subscribe {
                            it?.apply {
                                ZZXMiscUtils.write(ZZXMiscUtils.RGB_LED, this)
                            }
                        }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCaptureRatio() {
        val index = mSetting.getPhotoRatio()
        val ratio = mCaptureRation[index]
        ratio.split("x").let {
            Timber.e("${it[0]}x${it[1]}")
            mCameraPresenter.initCaptureParams(it[0].toInt(), it[1].toInt())
        }
    }

    override fun takeOneShot(needResult: Boolean) {
        mNeedResult = needResult
        mCapturing.set(true)
        controlLed()
        SoundPlayer.getInstance().playSound(mContext, R.raw.take_pic)
        VibrateUtil(mContext).vibrateOneShot()
        mCameraPresenter.takePicture()
    }

    override fun takeDelayPicture(needResult: Boolean) {
        mDelayMode = true
        mBtnCap.setImageResource(R.drawable.btn_record_video_stop)
        val timer = mSetting.getPhotoTimerCount()
        startTimer(timer)
        mDisposable = Observable.just(Unit)
                .delay(timer.toLong(), TimeUnit.SECONDS)
                .observeOn(Schedulers.newThread())
                .subscribe {
                    takeOneShot(mNeedResult)
                }
    }

    override fun takeIntervalPicture(needResult: Boolean) {
        mIntervalMode.set(true)
        mBtnCap.setImageResource(R.drawable.btn_record_video_stop)
        mIntervalCount = mSetting.getPhotoIntervalCount()
        takeOneShot(needResult)

        /*mDisposable = Observable.interval(0L, mIntervalCount.toLong(), TimeUnit.SECONDS)
                .observeOn(Schedulers.newThread())
                .subscribe {
                    startCapture()
                }*/

    }

    private fun startTimer(count: Int, needRepeat: Boolean = false) {
        mTimerDisposable?.dispose()
        var timerCount = count
        mTimerDisposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
//                    Timber.e("doOnSubscribe()")
                    mIvTimer?.visibility = View.VISIBLE
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (timerCount < 0) {
                        if (needRepeat) {
                            timerCount = count
                        } else {
                            return@subscribe
                        }
                    }
                    val ic = when (timerCount) {
                        10  -> R.drawable.ic_10
                        9   -> R.drawable.ic_9
                        8   -> R.drawable.ic_8
                        7   -> R.drawable.ic_7
                        6   -> R.drawable.ic_6
                        5   -> R.drawable.ic_5
                        4   -> R.drawable.ic_4
                        3   -> R.drawable.ic_3
                        2   -> R.drawable.ic_2
                        1   -> R.drawable.ic_1
                        else -> -1
                    }
                    if (timerCount == 1) {
                        VibrateUtil.getInstance(mContext).vibrateOneShot()
                    }
                    if (timerCount > 0) {
                        mIvTimer?.setImageResource(ic)
                        mTimerAnimation.start()
                    }
                    timerCount--
                }
    }

    override fun takeBurstPicture(needResult: Boolean) = singleThread {
        mNeedResult = needResult
        controlLed()
        mCapturing.set(true)
        SoundPlayer.getInstance().playSound(mContext, R.raw.take_pic)
        VibrateUtil(mContext).vibrateOneShot()
        val burstNum = mSetting.getPhotoContinuousCount()
        mCameraPresenter.takeBurstPicture(burstNum)
    }

    override fun clearPictureMode(needRefreshUI: Boolean) {
        Timber.w("clearPictureMode()")
        if (needRefreshUI) {
            FlowableUtil.setMainThread(
                    Consumer {
                        mBtnCap.setImageResource(R.drawable.btn_take_photo)
                        mIvTimer?.apply {
                            visibility = View.INVISIBLE
                        }
                        mCameraPresenter.getRecordView().manualCaptureFinish()
                    }
            )
        }
        mNeedResult = false
        mUserCapturing.set(false)
        mTimerDisposable?.dispose()
        mTimerDisposable = null
        mIntervalMode.set(false)
        mDelayMode = false
        mDisposable?.dispose()
        mDisposable = null
        mCaptureCallback?.onCaptureFinish()
    }

    override fun isIntervalOrDelayMode(): Boolean {
        return mIntervalMode.get() || mDelayMode
    }

    override fun isCapturing(): Boolean {
        return mCapturing.get()
    }

    override fun isUserCapturing(): Boolean {
        return mUserCapturing.get()
    }

    override fun release() {
    }

    inner class PictureCallback: ICameraManager.PictureCallback {

        override fun onCaptureError(errorCode: Int) {
            if (errorCode == ICameraManager.PictureCallback.ERROR_CODE_NOT_SUPPORT_VIDEO_CAPTURE) {
                mCapturing.set(false)
            }
            mNeedResult = false
            mCaptureCallback?.onCaptureError(errorCode)
        }

        override fun onCaptureStart() {
        }

        override fun onCaptureResult(buffer: ByteArray) {
            try {
//                var file: File? = null
                CommonConst.getPicDir(mContext)!!.apply {
                    if (FileUtil.checkDirExist(this, true)) {
                        mPictureFile = File(this, FileNameUtils.getPictureName(FileNameUtils.TYPE_PIC_PRE, "${DeviceUtils.getUserNum(mContext)}_"))
                    } else {
                        mCaptureCallback?.onCaptureError(IRecorder.IRecordCallback.ERROR_CODE_FILE_WRITE_DENIED)
                        return
                    }
                }
                mFileSaver?.savePhotoFile(buffer, mPictureFile)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun checkNeedBroadcast(needReset: Boolean = true) {
            if (mNeedResult) {
                mContext.sendBroadcast(Intent(ACTION_CAPTURE_RESULT).apply {
                    putExtra(RESULT_PIC_PATH, mPictureFile?.absolutePath)
                })
            }
            if (needReset) {
                mNeedResult = false
            }
        }

        override fun onCaptureDone() {
            Timber.e("mCaptureCallback(). mPictureFile = $mPictureFile")
            mFileSaver?.savePhotoFile(null, null)
            mFileSaver?.waitDone()
            mCaptureCallback?.onCaptureDone(mPictureFile)
            if (mDelayMode) {
                checkNeedBroadcast()
                clearPictureMode()
            } else {
                if (mIntervalMode.get()) {
                    checkNeedBroadcast(false)
                    if (FileUtil.getDirFreeSpaceByMB(FileUtil.getExternalStoragePath(mContext)) <= 50) {
                        clearPictureMode()
                        return
                    }
                    startTimer(mIntervalCount)
                    mDisposable = Observable.just(Unit)
                        .delay(mIntervalCount.toLong(), TimeUnit.SECONDS)
                        .observeOn(Schedulers.newThread())
                        .subscribe {
                            takeOneShot(mNeedResult)
                        }
                } else {
                    checkNeedBroadcast()
                    mUserCapturing.set(false)
                    mCameraPresenter.getRecordView().manualCaptureFinish()
                    mCaptureCallback?.onCaptureFinish()
                }
            }
            Observable.just(Unit)
                    .delay(300, TimeUnit.MILLISECONDS)
                    .subscribe {
                        mCapturing.set(false)
                    }
        }

    }


}