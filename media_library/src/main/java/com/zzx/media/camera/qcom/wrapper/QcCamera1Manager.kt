package com.zzx.media.camera.qcom.wrapper

import android.hardware.Camera
import com.zzx.media.camera.v1.manager.Camera1Manager
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2022/7/14.
 */
class QcCamera1Manager: Camera1Manager() {


    override fun setPictureBurstMode(pictureCount: Int) {
        Timber.w("setPictureContinuousMode. pictureCount = $pictureCount; mBurstMode = $mBurstMode")
        mContinuousShotCount = pictureCount
        if (!mBurstMode) {
            mBurstMode = true
            mParameters?.apply {
                if (isZSLModeSupported(this)) {
                    Timber.d("ZSLMode is Supported!!!")
                    set(KEY_QC_ZSL, ON)
                    ParametersWrapper.setCameraMode(this, QC_CAMERA_MODE_ZSL)
                }
                set(KEY_PICTURE_FORMAT, PIXEL_FORMAT_JPEG)
                set(BURST_MODE_QC, ON)
                set(BURST_SNAP_NUM, pictureCount)
            }
            setParameter()
            CameraWrapper.setLongShot(mCamera, true)
//            restartPreview()
        } else {
            mParameters?.apply {
                set(BURST_MODE_QC, OFF)
            }
            setParameter()
        }
    }

    override fun setPictureNormalMode() {
    }

    private fun isZSLModeSupported(parameters: Camera.Parameters): Boolean {
        return ParametersWrapper.getSupportedZSLModes(parameters) != null
    }

    companion object {
        // Parameter key suffix for supported values.
        const val SUPPORTED_VALUES_SUFFIX = "-values"

        const val BURST_MODE_QC    = "long-shot"
        const val BURST_SNAP_NUM    = "num-snaps-per-shutter"

        //零秒快拍模式
        const val KEY_QC_ZSL    = "zsl"
        const val ON    = "on"
        const val OFF   = "off"

        //若使用ZSL模式,需要将图像格式设置为jpeg.Raw格式不支持该模式.
        const val KEY_PICTURE_FORMAT = "picture-format"
        const val PIXEL_FORMAT_JPEG = "jpeg"


        const val KEY_QC_CAMERA_MODE = "camera-mode"
        const val QC_CAMERA_MODE_ZSL    = 1
        const val QC_CAMERA_MODE_NORMAL = 0
    }

}