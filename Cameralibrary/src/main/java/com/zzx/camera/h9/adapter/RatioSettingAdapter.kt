package com.zzx.camera.h9.adapter

import android.content.Context
import android.hardware.Camera
import android.widget.CheckBox
import android.widget.TextView
import butterknife.BindView
import com.tomy.lib.ui.adapter.BindListAdapter
import com.zzx.camera.R
import com.zzx.camera.data.HCameraSettings

/**@author Tomy
 * Created by Tomy on 2018/10/21.
 */
class RatioSettingAdapter(context: Context, ratioArrayId: Int, var cameraMode: Int): BindListAdapter<String, RatioSettingAdapter.RatioViewHolder>() {

    private var mPosition = 0

    private val mSetting = HCameraSettings(context)

    private val mRatioArray by lazy {
        context.resources.getStringArray(ratioArrayId).toList()
    }

    init {
        initData()
        addData(mRatioArray)
    }

    fun initData() {
        mPosition = when (cameraMode) {
            Camera.CameraInfo.CAMERA_FACING_BACK -> mSetting.getVideoRatioBack()
            Camera.CameraInfo.CAMERA_FACING_FRONT -> mSetting.getVideoRatioFront()
            else -> mSetting.getPhotoRatio()
        }
//        mPosition = if (cameraMode) mSetting.getPhotoRatio() else mSetting.getVideoRatio()
    }

    fun setChecked(position: Int) {
        mPosition = position
        when (cameraMode) {
            Camera.CameraInfo.CAMERA_FACING_BACK -> {
                mSetting.setVideoRatioBack(position)
                mSetting.setVideoRatio(position)
            }
            Camera.CameraInfo.CAMERA_FACING_FRONT -> {
                mSetting.setVideoRatioFront(position)
                mSetting.setVideoRatio(position + 1)
            }
            else -> mSetting.setPhotoRatio(position)
        }
//        if (cameraMode) mSetting.setPhotoRatio(position) else mSetting.setVideoRatio(position)
        notifyDataSetChanged()
    }


    override fun getLayoutId(): Int {
        return R.layout.ratio_setting
    }

    override fun getHolder(): RatioViewHolder {
        return RatioViewHolder()
    }

    inner class RatioViewHolder: BindListAdapter.BaseHolder<String>() {

        @BindView(R.id.check_box)
        lateinit var mCheckBox: CheckBox

        @BindView(R.id.tv_ratio)
        lateinit var mTvRatio: TextView

        override fun bindToItem(position: Int, item: String) {
            mCheckBox.isChecked = mPosition == position
            mTvRatio.text = item
        }

    }

    companion object {
        const val CAMERA_MODE_PHOTO = -1
    }

}