package com.tomy.lib.ui.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.SeekBar
import com.tomy.lib.ui.R
import com.tomy.lib.ui.databinding.VideoPlayerBinding
import com.zzx.utils.broadcast.BaseBroadcastReceiver
import com.zzx.utils.file.FileUtil
import com.zzx.utils.media.MediaInfo
import com.zzx.utils.rxjava.fixedThread
import com.zzx.utils.rxjava.toComposeSubscribe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import java.io.FileFilter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**@author Tomy
 * Created by Tomy on 2018/7/11.
 */
class VideoFragment: BaseFragmentViewBind<VideoPlayerBinding>(), View.OnClickListener {

    private var mFilePath: String = ""

    private var mSurfaceTexture: SurfaceTexture? = null

    private val mBaseBroadcastReceiver by lazy {
        BaseBroadcastReceiver(mContext).apply {
            addAction(Intent.ACTION_SHUTDOWN)
        }
    }

    private var mSeekBarDisposable: Disposable? = null

    private var mSeekDisposable: Disposable? = null

    private var mAutoHideDisposable: Disposable? = null

    private var mSpeedLevel = SPEED_MIN_LEVEL

    private val mMediaPlayer = MediaPlayer()

    private var mStatus = Status.RELEASED

    private var mSavedDuration = 0
    private var mSavedSpeed = 0
    private var mSavedPlaying = true

    private val mTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("GMT+0")
    }

    private var mFileArray: ArrayList<File>? = null
    private var mIndex = 0

    inner class TextureListener: TextureView.SurfaceTextureListener {

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            Timber.v("onSurfaceTextureDestroyed()")
            mSurfaceTexture = null
            return true
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        }

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            Timber.v("onSurfaceTextureAvailable(): $width x $height")
            mSurfaceTexture = surface
            mSurfaceTexture!!.setDefaultBufferSize(width, height)
            initMediaPlayer()
            preparePlayer()
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        }

    }

    fun setFilePath(filePath: String) {
        Timber.v("$this: setFilePath(): $filePath")
        mFilePath = filePath
        initFileList()
        startPlay()
    }

    private fun initMediaPlayer() {
        mMediaPlayer.apply {
            setSurface(Surface(mSurfaceTexture))
            val mediaListener = MediaListener()
            setOnErrorListener(mediaListener)
            setOnInfoListener(mediaListener)
            setOnPreparedListener(mediaListener)
            setOnSeekCompleteListener(mediaListener)
            setOnCompletionListener(mediaListener)
        }
        mStatus = Status.IDLE
        Timber.v("initMediaPlayer()")
    }

    /**
     * @see startPlay
     */
    private fun preparePlayer() {
        Timber.v("$this: mFilePath = $mFilePath")
        if (mSurfaceTexture != null && mStatus == Status.IDLE && mFilePath.isNotEmpty()) {
            mStatus = Status.PREPARED
            val file = File(mFilePath)
            mBinding?.fileName?.text    = file.nameWithoutExtension
            try {
                mMediaPlayer.setDataSource(mFilePath)
                mMediaPlayer.prepareAsync()
            } catch (e: Exception) {
                e.printStackTrace()
                mStatus = Status.ERROR
                resetPlay()
                showToast(R.string.error_video_file)
            }

        }
    }

    private fun seekTo(time: Int) {
        mMediaPlayer.seekTo(time)
    }

    private fun pause() {
        if (mStatus == Status.PLAYING) {
            mStatus = Status.PAUSED
            mBinding?.btnPlay?.setImageResource(R.drawable.btn_play)
            stopAutoSeek()
            mMediaPlayer.pause()
        }
    }

    /**
     * @see startPlay
     */
    private fun resume() {
        mBinding?.apply {
            if (mStatus == Status.PAUSED || mStatus == Status.PREPARED) {
                if (mSavedDuration > 0) {
                    mMediaPlayer.seekTo(mSavedDuration)
                    seekBar.progress = mSavedDuration
                    mSavedDuration = 0
                }
                if (mSavedSpeed > 0) {
                    mSpeedLevel = mSavedSpeed
                    mSavedSpeed = 0
                }
                if (mSavedPlaying) {
                    mStatus = Status.PLAYING
                    btnPlay.setImageResource(R.drawable.btn_pause)
                    mMediaPlayer.start()
                    setSpeed(false)
                    startAutoSeek()
                }
            }
        }
    }

    private fun startAutoSeek() {
        mSeekBarDisposable?.dispose()
        mSeekBarDisposable = null
        mSeekBarDisposable = Observable.interval(1, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .toComposeSubscribe(
                        {
                            val currentPosition = mMediaPlayer.currentPosition
                            mBinding?.seekBar?.progress = currentPosition
                            Timber.v("currentPosition = $currentPosition")
                        }
                )
    }

    private fun startSpeedPlay() {
        if (mSeekDisposable != null) {
            return
        }
        mSeekDisposable = Observable.interval(0, 500, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.computation())
                .toComposeSubscribe(
                        {
                            if (mStatus != Status.PLAYING) {
                                return@toComposeSubscribe
                            }
                            val currentPosition = mMediaPlayer.currentPosition
                            val speedValue = BASE_SPEED.shl(mSpeedLevel - 1)
                            val add = speedValue * 500
                            val progress = currentPosition + add
                            seekTo(progress)
                        }
                )
    }

    private fun stopSpeedPlay() {
        mSeekDisposable?.dispose()
        mSeekDisposable = null
    }

    private fun stopAutoSeek() {
        mSeekBarDisposable?.dispose()
        mSeekBarDisposable = null
    }

    private fun resetPlay() {
        Timber.d("resetPlay.mStatus = $mStatus")
        if (mStatus != Status.RELEASED && mStatus != Status.IDLE) {
            setSpeed(true)
            stopAutoSeek()
            mBinding?.btnPlay?.setImageResource(R.drawable.btn_play)
            initSeekBar(if (mStatus != Status.ERROR) mMediaPlayer.duration else 0)
            if (mStatus != Status.ERROR) {
                mMediaPlayer.reset()
            }
            mStatus = Status.IDLE
            mSavedPlaying = false
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_pre    -> clickPre()
            R.id.btn_play   -> togglePlay()
            R.id.btn_next   -> clickNext()
        }
    }

    fun toggleInfoShow() {
        if (mBinding?.infoBar?.visibility == View.VISIBLE) {
            hideInfo()
        } else {
            showInfo()
        }
    }

    private fun showInfo() {
        mBinding?.apply {
            infoBar.visibility = View.VISIBLE
            seekBarContainer.visibility = View.VISIBLE
            mAutoHideDisposable?.dispose()
            mAutoHideDisposable = Observable.just(Unit)
                .delay(3000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .toComposeSubscribe({
                    hideInfo()
                })
        }

    }

    private fun hideInfo() {
        mAutoHideDisposable?.dispose()
        mAutoHideDisposable = null
        mBinding?.apply {
            infoBar.visibility = View.INVISIBLE
            seekBarContainer.visibility = View.INVISIBLE
        }
    }

    fun togglePlay() {
        if (mMediaPlayer.isPlaying) {
            pause()
        } else {
            mSavedPlaying = true
            startPlay()
        }
    }

    private var mPreClickTime = 0L

    fun toggleSpeed() {
        if (mStatus == Status.RELEASED || mStatus == Status.ERROR) {
            return
        }
        mSavedPlaying = true
        val currentTime = SystemClock.elapsedRealtime()
        if ((currentTime - mPreClickTime) < 500) {
            return
        }
        mPreClickTime = currentTime
        Timber.e("mSpeedLevel = $mSpeedLevel")
        if (mStatus == Status.IDLE || mStatus == Status.RELEASED) {
            return
        }
        if (mSpeedLevel < SPEED_MAX_LEVEL) {
            mSpeedLevel++
            setSpeed(false)
        } else {
            setSpeed(true)
        }
    }


    private fun setSpeed(reset: Boolean) {
        try {
            if (reset) {
                mSpeedLevel = SPEED_MIN_LEVEL
            }
            if (mSpeedLevel == SPEED_MIN_LEVEL) {
                stopSpeedPlay()
            } else {
                startSpeedPlay()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        startPlay()
    }

    private fun startPlay() {
        showInfo()
        when (mStatus) {
            Status.IDLE -> preparePlayer()
            Status.PAUSED, Status.PREPARED -> resume()
            else -> {}
        }
    }

    fun clickPre() {
        mFileArray?.apply {
            if (mIndex in (size - 1) downTo 1) {
                disableBtn()
                resetPlay()
                mSavedPlaying = true
                mFilePath = get(--mIndex).absolutePath
                startPlay()
            } else {
                showToast(R.string.first_file)
            }
        }
    }

    private fun disableBtn() {
        mBinding?.apply {
            btnPre.isClickable     = false
            btnNext.isClickable    = false
            btnPlay.isClickable    = false
        }
    }

    private fun enableBtn() {
        mBinding?.apply {
            btnPre.isClickable     = true
            btnNext.isClickable    = true
            btnPlay.isClickable    = true
        }
    }

    fun clickNext() {
        mFileArray?.apply {
            if (mIndex in 0 until size - 1) {
                disableBtn()
                resetPlay()
                mSavedPlaying = true
                mFilePath = get(++mIndex).absolutePath
                startPlay()
            } else {
                showToast(R.string.last_file)
            }
        }
    }

    inner class MediaListener: MediaPlayer.OnPreparedListener,
            MediaPlayer.OnErrorListener,
            MediaPlayer.OnCompletionListener,
            MediaPlayer.OnInfoListener,
            MediaPlayer.OnSeekCompleteListener {
        override fun onPrepared(mp: MediaPlayer?) {
            initSeekBar(mMediaPlayer.duration)
            mSpeedLevel = SPEED_MIN_LEVEL
            startPlay()
            enableBtn()
        }

        override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
            Timber.e("what = $what; extra = $extra")
            resetPlay()
            enableBtn()
            return true
        }

        override fun onCompletion(mp: MediaPlayer?) {
            Timber.e("onCompletion")
            resetPlay()
        }

        override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
            return true
        }

        override fun onSeekComplete(mp: MediaPlayer?) {
        }

    }

    /**
     * 初始化布局.
     * */
    override fun initView(root: View) {
        Timber.w("initView(): $mBinding")
        mBinding?.apply {
            listener = this@VideoFragment
            textureView.surfaceTextureListener = TextureListener()
            seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    if (fromUser && (mStatus == Status.IDLE || mStatus == Status.RELEASED)) {
                        seekBar.progress = 0
                        return
                    }
                    val tmp = progress % 1000
                    var pro = progress
                    if (tmp > 800) {
                        pro += (1001 - tmp)
                    }
                    if (fromUser) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            seekTo(pro)
                        }
                        showInfo()
                    }
                    tvPlayTime.text = mTimeFormat.format(pro.toLong())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.w("onViewCreated()")
        savedInstanceState?.apply {
            mSavedDuration = getInt(LAST_DURATION, 0)
            mSavedSpeed = getInt(LAST_SPEED, 0)
            mSavedPlaying   = getBoolean(LAST_PLAYING, false)
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        Timber.w("onSaveInstanceState.mStatus = $mStatus")
        if (mStatus == Status.PAUSED || mStatus == Status.PLAYING || mStatus == Status.PREPARED) {
            val position = mMediaPlayer.currentPosition
            Timber.w("lastDuration = $position; lastSpeed = $mSpeedLevel")
            outState.putInt(LAST_DURATION, position)
            outState.putInt(LAST_SPEED, mSpeedLevel)
            outState.putBoolean(LAST_PLAYING, mStatus == Status.PLAYING)
            pause()
        }
    }


    private fun initSeekBar(max: Int) {
        Timber.e("duration = $max")
        mBinding?.apply {
            seekBar.progress = 0
            seekBar.max = max
            tvPlayTime.text     = mTimeFormat.format(0)
            tvTotalTime.text    = mTimeFormat.format(max)
        }
    }

    /**
     * 创建时初始化成员变量
     * */
    override fun initMember() {
        Timber.v("initMember()")
        mBaseBroadcastReceiver.setReceiver(ShutdownReceiver())
        if (mFilePath.isEmpty()) {
            mFilePath = arguments?.getString(FILE_PATH, "") ?: ""
        }
        initFileList()
    }

    private fun initFileList() = fixedThread {
        if (mFilePath.isEmpty()) {
            return@fixedThread
        }
        val file = File(mFilePath)
        mFileArray = FileUtil.sortDirTime(file.parentFile, false, filter = FileFilter {
            return@FileFilter it.extension == MediaInfo.FileSuffix.MP4.value
                    || it.extension == MediaInfo.FileSuffix.AVI.value
                    || it.extension == MediaInfo.FileSuffix.MKV.value
        })
        mIndex = mFileArray!!.indexOf(file)
    }

    inner class ShutdownReceiver: BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            mStatus = Status.RELEASED
            mMediaPlayer.reset()
            mMediaPlayer.release()
        }

    }

    /**
     * 布局销毁
     * */
    override fun destroyView() {
        Timber.w("destroyView()")
        resetPlay()
    }

    /**
     * 从Activity上分离时释放成员变量
     * */
    override fun releaseMember() {
        Timber.v("${this.javaClass.simpleName} releaseMember()")
        mBaseBroadcastReceiver.unregisterReceiver()
        mStatus = Status.RELEASED
        mMediaPlayer.release()
    }

    enum class Status {
        IDLE,
        PREPARED,
        PLAYING,
        PAUSED,
        RELEASED,
        ERROR
    }

    companion object {
        const val LAST_DURATION = "lastDuration"
        const val LAST_SPEED = "lastSpeed"
        const val LAST_PLAYING  = "lastPlaying"
        const val SPEED_MAX_LEVEL   = 5
        const val SPEED_MIN_LEVEL   = 0
        const val BASE_SPEED        = 0x1

        /**
         * FILE upload
         */
        const val EXTRA_PATH    = "value"
        const val EXTRA_ID      = "id"
        const val EXTRA_TYPE    = "type"
        const val TYPE_PIC      = 0
        const val TYPE_VIDEO    = 1
        const val TYPE_AUDIO    = 2

        const val FILE_PATH = "filePath"
        const val FILE_TYPE = "fileType"
    }

    override fun getViewBindingClass(): Class<out VideoPlayerBinding> {
        return VideoPlayerBinding::class.java
    }

}