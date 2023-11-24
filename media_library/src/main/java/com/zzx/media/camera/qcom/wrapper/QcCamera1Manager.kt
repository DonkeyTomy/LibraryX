package com.zzx.media.camera.qcom.wrapper

import android.hardware.Camera
import com.zzx.media.camera.CameraCore
import com.zzx.media.camera.ICameraManager
import com.zzx.media.camera.v1.manager.Camera1Manager
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2022/7/14.
 */
class QcCamera1Manager: Camera1Manager() {

    private var mLastPictureCount = -1

    private val mLongShotShutterCallback = LongShotShutterCallback()

    override fun setPictureBurstMode(pictureCount: Int) {
        Timber.w("setPictureContinuousMode. pictureCount = $pictureCount; mBurstMode = $mBurstMode")
        mLastPictureCount = -1
        if (!mBurstMode) {
            mBurstMode = true
            mParameters?.apply {
                set(KEY_QC_ZSL, ON)
                set(KEY_QC_CAMERA_MODE, QC_CAMERA_MODE_ZSL)
                set(BURST_MODE_QC, ON)
//                set(KEY_QC_ZSL, ON)
//                if (isZSLModeSupported(this)) {
//                    Timber.d("ZSLMode is Supported!!!")
//                    ParametersWrapper.setCameraMode(this, QC_CAMERA_MODE_ZSL)
//                }
//                set(BURST_SNAP_NUM, pictureCount)
//                restartPreview()
//                Timber.i(flatten())
                CameraWrapper.setLongShot(mCamera, true)
                remove(KEY_QC_LEGACY_BURST)
                setParameter()
            }
            setShutterCallback(mLongShotShutterCallback)
//            restartPreview()
        }/* else {
            mParameters?.apply {
                set(BURST_MODE_QC, OFF)
            }
            setParameter()
        }*/
    }

    override fun setPictureNormalMode() {
        Timber.d("setPictureNormalMode")
        if (mBurstMode) {
            mBurstMode = false
            mParameters?.apply {
                set(KEY_QC_ZSL, ON)
                set(KEY_QC_CAMERA_MODE, QC_CAMERA_MODE_ZSL)
                set(BURST_MODE_QC, OFF)
                Timber.i(flatten())
                CameraWrapper.setLongShot(mCamera, false)
                setParameter()
            }
            setShutterCallback(null)
        }
    }

    private fun isZSLModeSupported(parameters: Camera.Parameters): Boolean {
        return ParametersWrapper.getSupportedZSLModes(parameters) != null
    }

    private inner class LongShotShutterCallback: ICameraManager.ShutterCallback {

        override fun onShutter() {
            Timber.v("Tomy capture: $mPictureCount [$mContinuousShotCount]")
            mLastPictureCount = mPictureCount
            if (mPictureCount >= mContinuousShotCount - 1) {
                /*mParameters?.apply {
                    Timber.d("setLongShot false")
                    setShutterCallback(null)
                    mBurstMode = false
                    CameraWrapper.setLongShot(mCamera, false)
                    setParameter()
                }*/
                return
            }
            try {
                Timber.e("Tomy takePicture start")
                mCamera!!.takePicture(mShutterCamera1Callback, null, mPictureDataCallback)
            } catch (e: Exception) {
                if (!mCameraCore.isRecording()) {
                    mCameraCore.setStatus(CameraCore.Status.PREVIEW)
                } else {
                    mCameraCore.setStatus(CameraCore.Status.RECORDING)
                }
                mPictureCallback?.onCaptureDone()
            }
        }

    }

    companion object {
        // Parameter key suffix for supported values.
        const val SUPPORTED_VALUES_SUFFIX = "-values"

        const val BURST_MODE_QC    = "long-shot"
        const val BURST_SNAP_NUM    = "num-snaps-per-shutter"

        const val KEY_QC_LEGACY_BURST = "snapshot-burst-num"

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