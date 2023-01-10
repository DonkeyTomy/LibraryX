package com.zzx.camera.h9.view

import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.SystemClock
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.zzx.camera.R
import com.zzx.camera.R2
import com.zzx.camera.glide.GlideApp
import com.zzx.camera.utils.LedController
import com.zzx.camera.view.IRecordView
import com.zzx.log.LogReceiver
import com.zzx.media.utils.ThumbnailUtil
import com.zzx.utils.ExceptionHandler
import com.zzx.utils.TTSToast
import com.zzx.utils.alarm.SoundPlayer
import com.zzx.utils.alarm.VibrateUtil
import com.zzx.utils.rxjava.FlowableUtil
import com.zzx.utils.zzx.ZZXMiscUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**@author Tomy
 * Created by Tomy on 2018/10/17.
 */
class HRecordView(var mContext: Context, rootView: View): IRecordView() {
    @BindView(R2.id.btn_mode)
    lateinit var mBtnMode: ImageView

    @BindView(R2.id.btn_mode_switch)
    lateinit var mBtnModeSwitch: ImageView

    @BindView(R2.id.btn_rec)
    lateinit var mBtnRec: ImageView

    @BindView(R2.id.btn_camera_switch)
    lateinit var mBtnSwitchCamera: ImageView

    @BindView(R2.id.btn_thumb)
    lateinit var mBtnThumb: ImageView

    @BindView(R2.id.btn_ratio_switch)
    lateinit var mBtnRatio: ImageView

    @BindView(R2.id.tv_record_duration)
    lateinit var mTvDuration: TextView

    @BindView(R2.id.iv_recording_state)
    lateinit var mIvRecording: ImageView

    @BindView(R2.id.imp_icon)
    lateinit var mImpIcon: ImageView

    @BindView(R2.id.tv_record_error)
    lateinit var mTvRecordError: TextView

    @BindView(R2.id.iv_focus)
    lateinit var mIvFocus: ImageView

    private var mCount = 0

    private var mImpShow = false

    /** 是否已显示录像窗口 **/
    private var isShow = false

    @Volatile
    private var mIsManualCapturing = false

    private val mUnbinder: Unbinder = ButterKnife.bind(this, rootView)

    private val mDateFormat by lazy {
        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("GMT+0")
        }
    }

    private var mDisposable: Disposable? = null

    private val mRecordTimeObservable by lazy {
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    mTvDuration.visibility = View.VISIBLE
                }
                .doOnDispose {
                    mCount = 0
                    mTvDuration.visibility = View.INVISIBLE
                }
                .map {
                    mTvDuration.text = mDateFormat.format(mCount * 1000)
                    mCount++
//                    Timber.e("map.$it. mCount = $mCount thread = ${Thread.currentThread().name}")
                }
    }

    override fun setRecordTime(time: Int) {
        mCount = time
        Timber.d("setRecordTime. mCount = $mCount")
    }

    /**
     * @see stopRecord
     */
    override fun startRecord(needTTS: Boolean) {
        Timber.d("startRecord. isRecording = $mIsRecording")
        if (mIsRecording) {
            return
        }
        mStartTime = SystemClock.elapsedRealtime()
        mIsRecording = true
        if (needTTS) {
            mContext.sendBroadcast(Intent(LogReceiver.ACTION_RECORD_VIDEO))
            SoundPlayer.getInstance().playSound(mContext, R.raw.start_record)
            VibrateUtil.getInstance(mContext).vibrateOneShot()
            LedController.INSTANCE.startRecordVideo()
        }
        FlowableUtil.setMainThread {
//                    if (!needTTS) {
            mDisposable = mRecordTimeObservable.subscribe()
//                    }
            mTvRecordError.visibility = View.GONE
//        disableCameraBtn()
            mBtnRec.setImageResource(R.drawable.btn_record_video_stop)
            mBtnMode.isClickable = false
            mBtnModeSwitch.isClickable = false
            mBtnRatio.isClickable = false
            mBtnSwitchCamera.isClickable = false
            showRecordStatus()
        }
    }

    override fun showImpIcon(show: Boolean, needTTS: Boolean) {
        Timber.d("show: $show; mImpShow: $mImpShow; needTTS: $needTTS")
        if (mImpShow == show) {
            return
        }
        mImpShow = show
        if (needTTS && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (mContext.resources.configuration.locales[0].language == Locale.CHINESE.language) {
                SoundPlayer.getInstance().playSound(mContext, if (show) R.raw.imp_file_enabled else R.raw.imp_file_disabled)
            }
        }
        FlowableUtil.setMainThread {
            mImpIcon.visibility = if (show) View.VISIBLE else View.INVISIBLE
        }
    }

    override fun focusOnPoint(x: Float, y: Float) {
        mIvFocus.apply {
            visibility = View.VISIBLE
            setImageResource(R.drawable.ic_focus_nor_big)
            Timber.e("width = $width; height = $height")
            this.x = x - width / 2
            this.y = y - height
        }
    }

    override fun release() {
        mUnbinder.unbind()
    }

    override fun focusSuccess(success: Boolean) {
        Observable.just(Unit)
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    mIvFocus.setImageResource(if (success) R.drawable.ic_focus_success else R.drawable.ic_focus_fail)
                }
                .delay(200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mIvFocus.visibility = View.INVISIBLE
                }
    }

    override fun disableCameraBtn() {
        FlowableUtil.setMainThread {
            mBtnRec.isClickable = false
        }
    }

    override fun enableCameraBtn() {
        FlowableUtil.setMainThread {
            mBtnRec.isClickable = true
        }
    }

    /**
     * @see startRecord
     * @param needTTS Boolean
     */
    override fun stopRecord(needTTS: Boolean) {
        Timber.d("stopRecord. isRecording = $mIsRecording")
        if (!mIsRecording) {
            return
        }
        mStopTime = SystemClock.elapsedRealtime()
        mDisposable?.dispose()
        mDisposable = null
        if (needTTS) {
            mContext.sendBroadcast(Intent(LogReceiver.ACTION_RECORD_VIDEO).putExtra(LogReceiver.EXTRA_STATE, false))
            SoundPlayer.getInstance().playSound(mContext, R.raw.stop_record)
//            ZZXMiscUtils.toggleLed(ZZXMiscUtils.LED_GREEN, context = mContext, oneShot = true)
            LedController.INSTANCE.stopRecordVideo()
            VibrateUtil.getInstance(mContext).vibrateOneShot()
        }
        FlowableUtil.setMainThread {
            mIsRecording = false
            mBtnRec.setImageResource(R.drawable.btn_record)
            mBtnMode.isClickable = true
            mBtnModeSwitch.isClickable = true
            mBtnRatio.isClickable = true
            mBtnSwitchCamera.isClickable = true
            showRecordStatus()
            showImpIcon(false, needTTS)
        }

    }

    override fun isShow(show: Boolean) {
        isShow = show
//        showRecordStatus()
    }

    override fun takePictureFinish() {
        if (isRecording()) {
            return
        }
        FlowableUtil.setMainThread {
            mBtnRec.isClickable = true
            /*mBtnMode.isClickable = true
            mBtnModeSwitch.isClickable = true
            mBtnRatio.isClickable = true
            mBtnSwitchCamera.isClickable = true*/
        }
    }

    override fun takePictureStart() {
        if (isRecording()) {
            return
        }
        LedController.INSTANCE.takePic()
        FlowableUtil.setMainThread {
            mBtnRec.isClickable = false
            /*mBtnMode.isClickable = false
            mBtnModeSwitch.isClickable = false
            mBtnRatio.isClickable = false
            mBtnSwitchCamera.isClickable = false*/
        }

    }

    override fun manualCaptureStart() {
        Timber.w("manualCaptureFinish.isRecording = $mIsRecording")
        if (isRecording()) {
            return
        }
        mIsManualCapturing = true
        FlowableUtil.setMainThread {
            mBtnMode.isClickable = false
            mBtnModeSwitch.isClickable = false
            mBtnRatio.isClickable = false
            mBtnSwitchCamera.isClickable = false
        }
    }

    override fun manualCaptureFinish() {
        Timber.w("manualCaptureFinish.isRecording = $mIsRecording")
        if (isRecording()) {
            return
        }
        mIsManualCapturing = false
        FlowableUtil.setMainThread {
            mBtnMode.isClickable = true
            mBtnModeSwitch.isClickable = true
            mBtnRatio.isClickable = true
            mBtnSwitchCamera.isClickable = true
        }
    }

    override fun isManualCapturing(): Boolean {
        return mIsManualCapturing
    }

    /**
     * 为了解决几率性出现动画图标变成一个点.
     * 只有在显示录像窗口以及正在录像的情况下才会显示录像动画.
     * */
    override fun showRecordStatus() {
        Timber.e("showRecordStatus: isShow = $isShow; isRecording = ${isRecording()}")
        if (isRecording()) {
            mIvRecording.visibility    = View.VISIBLE
            (mIvRecording.drawable as AnimationDrawable).start()
        } else {
            mIvRecording.visibility    = View.INVISIBLE
            (mIvRecording.drawable as AnimationDrawable).stop()
        }
    }

    override fun noticeRecording(recording: Boolean) {
        FlowableUtil.setMainThread {
            if (recording) {
                mIvRecording.visibility = View.VISIBLE
                (mIvRecording.drawable as AnimationDrawable).start()
            } else {
                mIvRecording.visibility = View.INVISIBLE
                (mIvRecording.drawable as AnimationDrawable).stop()
            }
        }
    }

    override fun showThumb(file: File?) {
        try {
            file?.apply {
                Timber.e("showThumb: path = $absoluteFile; exists = ${exists()}; isDirectory = $isDirectory")
                if (!exists() || isDirectory) {
                    return
                }
                when (extension) {
                    "mp4", "avi", "mkv" -> {
                        Observable.just(this)
                                .observeOn(Schedulers.computation())
                                .map {
                                    ThumbnailUtil.getVideoThumbnail(this.absolutePath, 72, 72)
                                }
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    it?.apply {
//                                        mBtnThumb.setImageBitmap(this)
                                        GlideApp.with(mContext)
                                                .asBitmap()
                                                .skipMemoryCache(true)
                                                .load(this)
                                                .into(mBtnThumb)
                                    }
                                }
                    }
                    "png", "jpg" -> {
                        FlowableUtil.setMainThread {
                            GlideApp.with(mContext)
                                .asBitmap()
                                .override(72, 72)
                                .load(this)
                                .skipMemoryCache(true)
//                                    .thumbnail(0.2f)
                                .into(mBtnThumb)
                        }
                    }
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun recordError(msgId: Int, needTTS: Boolean) {
        recordError(mContext.getString(msgId), needTTS)
    }

    override fun recordError(msg: String, needTTS: Boolean) {
        ExceptionHandler.getInstance().saveLog2File(msg)
        Timber.e("errorMsg = $msg")
        if (msg == mContext.getString(R.string.record_too_short)) {
            stopRecord(false)
            noticeRecording(false)
            enableCameraBtn()
            return
        }
        showImpIcon(false, needTTS)
        if (needTTS)
            VibrateUtil(mContext).start()
        FlowableUtil.setMainThread {
            noticeRecording(false)
            stopRecord()
            enableCameraBtn()
            Observable.just(Unit)
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    mTvRecordError.visibility = View.VISIBLE
                    mTvRecordError.text = msg
                }
                .delay(3500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mTvRecordError.visibility = View.GONE
                }
            if (needTTS)
                TTSToast.showToast(msg, needTTS = true)
        }
    }

    override fun showMsg(msgId: Int) {
        FlowableUtil.setMainThread {
            Observable.just(Unit)
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    mTvRecordError.visibility = View.VISIBLE
                    mTvRecordError.setText(msgId)
                }
                .delay(3500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mTvRecordError.visibility = View.GONE
                }

        }
    }
}