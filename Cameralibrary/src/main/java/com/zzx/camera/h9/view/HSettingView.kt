package com.zzx.camera.h9.view

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.hardware.Camera
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import com.tomy.lib.ui.view.preference.TPreferenceManager
import com.zzx.camera.R
import com.zzx.camera.data.HCameraSettings
import com.zzx.camera.h9.adapter.RatioSettingAdapter
import com.zzx.utils.rxjava.FlowableUtil
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

/**@author Tomy
 * Created by Tomy on 2018/10/18.
 */
class HSettingView(var context: Context, var modeAnchor: View, var ratioAnchor: ImageView) {

    private val mPreferenceManager = TPreferenceManager(context)

    private lateinit var mListViewPhotoRatio: ListView
    //前置摄像头分辨率
    private lateinit var mListViewVideoRatioFront: ListView
    private lateinit var mListViewVideoRatio: ListView
    private var mListViewPhotoMode: ListView? = null
    private var mListViewVideoMode: ListView? = null

    private var mCameraFace = Camera.CameraInfo.CAMERA_FACING_BACK

    private val mSetting by lazy {
        HCameraSettings(context)
    }

    private var mWindowWidth = 0

    private val mRatioAnchorWidth by lazy {
        ratioAnchor.measuredWidth / 2
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private val mPopWindow = PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT).apply {
        isFocusable = true
        setBackgroundDrawable(ColorDrawable(android.R.color.transparent))
        windowLayoutType = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR
    }

    private val mVideoRatioArray by lazy {
        val array = ArrayList<Drawable>()
        context.resources.obtainTypedArray(R.array.array_ratio_record_icon).apply {
            for (i in 0 until length()) {
                getDrawable(i)?.let { array.add(it) }
            }
            recycle()
        }
        return@lazy array
    }
    private val mVideoRatioArrayFront by lazy {
        val array = ArrayList<Drawable>()
        context.resources.obtainTypedArray(R.array.array_ratio_record_icon_front).apply {
            for (i in 0 until length()) {
                getDrawable(i)?.let { array.add(it) }
            }
            recycle()
        }
        return@lazy array
    }

    private val mPhotoRatioArray by lazy {
        val array = ArrayList<Drawable>()
        context.resources.obtainTypedArray(R.array.array_ratio_capture_icon).apply {
            for (i in 0 until length()) {
                getDrawable(i)?.let { array.add(it) }
            }
            recycle()
        }
        return@lazy array
    }

    init {
        init()
    }

    private fun init() {
        FlowableUtil.setBackgroundThreadMapMain<Unit>(
                 {
                    initView()
                },
                 {
                    bindView()
                    refreshRatioButton()
                }
        )
    }

    fun configurationChanged() {
        try {
            mPreferenceManager.removeAll()
            initMode()
            bindView()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun switchCamera(face : Int) {
        mCameraFace = face
        FlowableUtil.setMainThread {
            refreshRatioButton()
        }
    }

    fun refreshRatioButton() {
        val videoMode = mSetting.getCameraMode() == HCameraSettings.DEFAULT_CAMERA_MODE_VIDEO
        if (videoMode) {
            if (mCameraFace == Camera.CameraInfo.CAMERA_FACING_BACK) {
                val position = mSetting.getVideoRatioBack()
                mSetting.setVideoRatio(position)
                ratioAnchor.setImageDrawable(mVideoRatioArray[position])
            } else {
                val position = mSetting.getVideoRatioFront()
                mSetting.setVideoRatio(position + 1)
                ratioAnchor.setImageDrawable(mVideoRatioArrayFront[position])
            }
        } else {
            val position = mSetting.getPhotoRatio()
            ratioAnchor.setImageDrawable(mPhotoRatioArray[position])
        }
        Timber.e("videoMode = $videoMode")
//        ratioAnchor.setImageDrawable(if (videoMode) mVideoRatioArray[position] else mPhotoRatioArray[position])
    }

    private fun initMode() {
        mListViewPhotoMode = null
        mListViewVideoMode = null
        mListViewPhotoMode = ListView(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundResource(R.drawable.bg_left)
        }
        mListViewVideoMode = ListView(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundResource(R.drawable.bg_left)
        }
    }

    private fun initView() {
        initMode()

        mListViewPhotoRatio = ListView(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundResource(R.drawable.bg_camera_setting_dialog)
            adapter = RatioSettingAdapter(context, R.array.array_ratio_capture_txt, RatioSettingAdapter.CAMERA_MODE_PHOTO)
            setOnItemClickListener { _, _, position, _ ->
                (adapter as RatioSettingAdapter).setChecked(position)
                ratioAnchor.setImageDrawable(mPhotoRatioArray[position])
            }
        }
        mListViewVideoRatio = ListView(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundResource(R.drawable.bg_camera_setting_dialog)
            adapter = RatioSettingAdapter(context, R.array.array_ratio_record, Camera.CameraInfo.CAMERA_FACING_BACK)
            setOnItemClickListener { _, _, position, _ ->
                (adapter as RatioSettingAdapter).setChecked(position)
                ratioAnchor.setImageDrawable(mVideoRatioArray[position])
            }
        }
        mListViewVideoRatioFront = ListView(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundResource(R.drawable.bg_camera_setting_dialog)
            adapter = RatioSettingAdapter(context, R.array.array_ratio_record_front, Camera.CameraInfo.CAMERA_FACING_FRONT)
            setOnItemClickListener { _, _, position, _ ->
                (adapter as RatioSettingAdapter).setChecked(position)
                ratioAnchor.setImageDrawable(mVideoRatioArrayFront[position])
            }
        }

        context.resources.apply {
            val language = configuration.locales[0].language
            Timber.e("language = $language")
            mWindowWidth = if (language == Locale.CHINA.language) {
                displayMetrics.widthPixels * 3 / 6
            } else {
                displayMetrics.widthPixels * 3 / 6
            }
        }
    }

    private fun bindView() {
        mPreferenceManager.apply {
            inflatePreferenceScreen(R.xml.preference_record_set)
            bindPreferences(mListViewVideoMode!!)
        }
        mPreferenceManager.apply {
            inflatePreferenceScreen(R.xml.preference_capture_set)
            bindPreferences(mListViewPhotoMode!!)
        }
    }

    /**
     * 显示录像模式设置
     */
    fun showVideoMode() {
        Timber.d("showVideoMode()")
        showPopWindow(mListViewVideoMode!!, modeAnchor, -10, 10)
    }

    /**
     * 显示拍照模式设置
     */
    fun showPhotoMode() {
        Timber.d("showPhotoMode()")
        showPopWindow(mListViewPhotoMode!!, modeAnchor, -10, 10)
    }

    fun showPhotoRatio() {
        Timber.d("showPhotoRatio()")
        showRatio(mListViewPhotoRatio)
    }

    fun showVideoRatio() {
        Timber.d("showVideoRatio()")
        if (mCameraFace == Camera.CameraInfo.CAMERA_FACING_BACK) {
            showRatio(mListViewVideoRatio)
        } else {
            showRatio(mListViewVideoRatioFront)
        }
    }

    private fun showRatio(listView: ListView) {
        showRatioWindow(listView, ratioAnchor)
    }

    private var mHeight = 0

    private fun showPopWindow(listView: ListView, anchor: View, xOff: Int, yOff: Int) {
        listView.apply {
            divider = ColorDrawable(R.color.white)
            dividerHeight = 1
            measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            mHeight = measuredHeight * adapter.count
            /*viewTreeObserver.addOnGlobalLayoutListener {
                val intArray = IntArray(2)
                getLocationOnScreen(intArray)
                Timber.e("measuredHeight = $measuredWidth; x = ${intArray[0]}, y = ${intArray[1]}")
            }*/
            val intArray = IntArray(2)
            getLocationOnScreen(intArray)
//            Timber.e("measuredHeight = $measuredWidth; x = ${intArray[0]}, y = ${intArray[1]}")

        }
        mPopWindow.apply {
            contentView = listView
            width = mWindowWidth
            height = mHeight
            mPopWindow.showAsDropDown(anchor, xOff, yOff)
        }
    }

    private fun showRatioWindow(listView: ListView, anchor: View) {
        var width = 0
        listView.apply {
            isVerticalScrollBarEnabled = false
            divider = ColorDrawable(R.color.white)
            dividerHeight = 1
            measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            mHeight = measuredHeight * adapter.count
            width = measuredWidth
            /*viewTreeObserver.addOnGlobalLayoutListener {
                val intArray = IntArray(2)
                getLocationOnScreen(intArray)
                Timber.e("measuredHeight = $measuredWidth; x = ${intArray[0]}, y = ${intArray[1]}")
            }*/
            val intArray = IntArray(2)
            getLocationOnScreen(intArray)
//            Timber.e("measuredHeight = $measuredWidth; x = ${intArray[0]}, y = ${intArray[1]}")

        }
        mPopWindow.apply {
            contentView = listView
            this.width = width
            height = mHeight
            showAsDropDown(anchor, -width / 2 + mRatioAnchorWidth, 20)
        }
//        mPopWindow.showAtLocation(anchor, Gravity.CENTER, -width / 2, 20)
    }

}