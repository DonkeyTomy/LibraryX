/*
 * Copyright (c) 2017, The Linux Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above
 *    copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 *  * Neither the name of The Linux Foundation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.zzx.media.camera.qcom.wrapper

import android.annotation.SuppressLint
import android.hardware.Camera
import android.util.Log
import java.lang.reflect.Method

@SuppressLint("PrivateApi")
object ParametersWrapper : Wrapper() {
    private const val TAG = "ParametersWrapper"
    /*val FACE_DETECTION_ON = getFieldValue(
        getField(Camera.Parameters::class.java, "FACE_DETECTION_ON"), "off"
    )
    val FACE_DETECTION_OFF = getFieldValue(
        getField(Camera.Parameters::class.java, "FACE_DETECTION_OFF"), "off"
    )
    val ZSL_OFF = getFieldValue(
        getField(Camera.Parameters::class.java, "ZSL_OFF"), "off"
    )
    val ZSL_ON = getFieldValue(
        getField(Camera.Parameters::class.java, "ZSL_ON"), "on"
    )
    val TOUCH_AF_AEC_ON = getFieldValue(
        getField(Camera.Parameters::class.java, "TOUCH_AF_AEC_ON"), "touch-off"
    )
    val TOUCH_AF_AEC_OFF = getFieldValue(
        getField(Camera.Parameters::class.java, "TOUCH_AF_AEC_OFF"), "touch-off"
    )
    val DENOISE_OFF = getFieldValue(
        getField(Camera.Parameters::class.java, "DENOISE_OFF"), "denoise-off"
    )
    val DENOISE_ON = getFieldValue(
        getField(Camera.Parameters::class.java, "DENOISE_ON"), "denoise-off"
    )
    val ISO_AUTO = getFieldValue(
        getField(Camera.Parameters::class.java, "ISO_AUTO"), "auto"
    )
    val FOCUS_MODE_MANUAL_POSITION = getFieldValue(
        getField(Camera.Parameters::class.java, "FOCUS_MODE_MANUAL_POSITION"), "manual"
    )*/
    private var method_isPowerModeSupported: Method? = null
    fun isPowerModeSupported(parameters: Camera.Parameters?): Boolean {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no isPowerModeSupported")
            return false
        }
        var supported = false
        try {
            if (method_isPowerModeSupported == null) {
                method_isPowerModeSupported =
                    Camera.Parameters::class.java.getDeclaredMethod("isPowerModeSupported")
            }
            supported = method_isPowerModeSupported!!.invoke(parameters) as Boolean
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return supported
    }

    private var method_setPowerMode: Method? = null
    fun setPowerMode(parameters: Camera.Parameters?, value: String?) {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no setPowerMode")
            return
        }
        try {
            if (method_setPowerMode == null) {
                method_setPowerMode = Camera.Parameters::class.java.getDeclaredMethod(
                    "setPowerMode",
                    String::class.java
                )
            }
            method_setPowerMode!!.invoke(parameters, value)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private var method_getPowerMode: Method? = null
    fun getPowerMode(parameters: Camera.Parameters?): String? {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no getPowerMode")
            return null
        }
        var powerMode: String? = null
        try {
            if (method_getPowerMode == null) {
                method_getPowerMode =
                    Camera.Parameters::class.java.getDeclaredMethod("getPowerMode")
            }
            powerMode = method_getPowerMode!!.invoke(parameters) as String
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return powerMode
    }

    private var method_setCameraMode: Method? = null
    fun setCameraMode(parameters: Camera.Parameters?, cameraMode: Int) {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no setCameraMode")
            return
        }
        try {
            if (method_setCameraMode == null) {
                method_setCameraMode = Camera.Parameters::class.java.getDeclaredMethod(
                    "setCameraMode",
                    Int::class.javaPrimitiveType
                )
            }
            method_setCameraMode!!.invoke(parameters, cameraMode)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private var method_getSupportedIsoValues: Method? = null
    fun getSupportedIsoValues(parameters: Camera.Parameters?): List<String?>? {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no getSupportedIsoValues")
            return null
        }
        var supportedList: List<String?>? = null
        try {
            if (method_getSupportedIsoValues == null) {
                method_getSupportedIsoValues =
                    Camera.Parameters::class.java.getDeclaredMethod("getSupportedIsoValues")
            }
            supportedList = method_getSupportedIsoValues!!.invoke(parameters) as List<String?>
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return supportedList
    }

    private var method_getISOValue: Method? = null
    fun getISOValue(parameters: Camera.Parameters?): String? {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no getISOValue")
            return null
        }
        var isoValue: String? = null
        try {
            if (method_getISOValue == null) {
                method_getISOValue = Camera.Parameters::class.java.getDeclaredMethod("getISOValue")
            }
            isoValue = method_getISOValue!!.invoke(parameters) as String
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return isoValue
    }

    private var method_setISOValue: Method? = null
    fun setISOValue(parameters: Camera.Parameters?, iso: String?) {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no setISOValue")
            return
        }
        try {
            if (method_setISOValue == null) {
                method_setISOValue = Camera.Parameters::class.java.getDeclaredMethod(
                    "setISOValue",
                    String::class.java
                )
            }
            method_setISOValue!!.invoke(parameters, iso)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private var method_getSupportedAutoexposure: Method? = null
    fun getSupportedAutoexposure(parameters: Camera.Parameters?): List<String?>? {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no getSupportedAutoexposure")
            return null
        }
        var supportedList: List<String?>? = null
        try {
            if (method_getSupportedAutoexposure == null) {
                method_getSupportedAutoexposure =
                    Camera.Parameters::class.java.getDeclaredMethod("getSupportedAutoexposure")
            }
            supportedList = method_getSupportedAutoexposure!!.invoke(parameters) as List<String?>
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return supportedList
    }

    private var method_getAutoExposure: Method? = null
    fun getAutoExposure(parameters: Camera.Parameters?): String? {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no getAutoExposure")
            return null
        }
        var autoExposure: String? = null
        try {
            if (method_getAutoExposure == null) {
                method_getAutoExposure =
                    Camera.Parameters::class.java.getDeclaredMethod("getAutoExposure")
            }
            autoExposure = method_getAutoExposure!!.invoke(parameters) as String
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return autoExposure
    }

    private var method_setAutoExposure: Method? = null
    fun setAutoExposure(parameters: Camera.Parameters?, value: String?) {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no setAutoExposure")
            return
        }
        try {
            if (method_setAutoExposure == null) {
                method_setAutoExposure = Camera.Parameters::class.java.getDeclaredMethod(
                    "setAutoExposure",
                    String::class.java
                )
            }
            method_setAutoExposure!!.invoke(parameters, value)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private var method_getSupportedTouchAfAec: Method? = null
    fun getSupportedTouchAfAec(parameters: Camera.Parameters?): List<String?>? {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no getSupportedTouchAfAec")
            return null
        }
        var supportedList: List<String?>? = null
        try {
            if (method_getSupportedTouchAfAec == null) {
                method_getSupportedTouchAfAec =
                    Camera.Parameters::class.java.getDeclaredMethod("getSupportedTouchAfAec")
            }
            supportedList = method_getSupportedTouchAfAec!!.invoke(parameters) as List<String?>
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return supportedList
    }

    private var method_getTouchAfAec: Method? = null
    fun getTouchAfAec(parameters: Camera.Parameters?): String? {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no getTouchAfAec")
            return null
        }
        var touchAfAec: String? = null
        try {
            if (method_getTouchAfAec == null) {
                method_getTouchAfAec =
                    Camera.Parameters::class.java.getDeclaredMethod("getTouchAfAec")
            }
            touchAfAec = method_getTouchAfAec!!.invoke(parameters) as String
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return touchAfAec
    }

    private var method_setTouchAfAec: Method? = null
    fun setTouchAfAec(parameters: Camera.Parameters?, value: String?) {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no setTouchAfAec")
            return
        }
        try {
            if (method_setTouchAfAec == null) {
                method_setTouchAfAec = Camera.Parameters::class.java.getDeclaredMethod(
                    "setTouchAfAec",
                    String::class.java
                )
            }
            method_setTouchAfAec!!.invoke(parameters, value)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private var method_getSupportedSelectableZoneAf: Method? = null
    fun getSupportedSelectableZoneAf(parameters: Camera.Parameters?): List<String?>? {
        if (DEBUG) {
            Log.e(
                TAG,
                "Debug:" + Camera.Parameters::class.java + " no getSupportedSelectableZoneAf"
            )
            return null
        }
        var supportedList: List<String?>? = null
        try {
            if (method_getSupportedSelectableZoneAf == null) {
                method_getSupportedSelectableZoneAf =
                    Camera.Parameters::class.java.getDeclaredMethod("getSupportedSelectableZoneAf")
            }
            supportedList =
                method_getSupportedSelectableZoneAf!!.invoke(parameters) as List<String?>
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return supportedList
    }

    private var method_setSelectableZoneAf: Method? = null
    fun setSelectableZoneAf(parameters: Camera.Parameters?, value: String?) {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no setSelectableZoneAf")
            return
        }
        try {
            if (method_setSelectableZoneAf == null) {
                method_setSelectableZoneAf = Camera.Parameters::class.java.getDeclaredMethod(
                    "setSelectableZoneAf",
                    String::class.java
                )
            }
            method_setSelectableZoneAf!!.invoke(parameters, value)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private var method_getSupportedRedeyeReductionModes: Method? = null
    fun getSupportedRedeyeReductionModes(parameters: Camera.Parameters?): List<String?>? {
        if (DEBUG) {
            Log.e(
                TAG,
                "Debug:" + Camera.Parameters::class.java + " no getSupportedRedeyeReductionModes"
            )
            return null
        }
        var supportedList: List<String?>? = null
        try {
            if (method_getSupportedRedeyeReductionModes == null) {
                method_getSupportedRedeyeReductionModes =
                    Camera.Parameters::class.java.getDeclaredMethod("getSupportedRedeyeReductionModes")
            }
            supportedList =
                method_getSupportedRedeyeReductionModes!!.invoke(parameters) as List<String?>
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return supportedList
    }

    private var method_setRedeyeReductionMode: Method? = null
    fun setRedeyeReductionMode(parameters: Camera.Parameters?, value: String?) {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no setRedeyeReductionMode")
            return
        }
        try {
            if (method_setRedeyeReductionMode == null) {
                method_setRedeyeReductionMode = Camera.Parameters::class.java.getDeclaredMethod(
                    "setRedeyeReductionMode", String::class.java
                )
            }
            method_setRedeyeReductionMode!!.invoke(parameters, value)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private var method_getSupportedDenoiseModes: Method? = null
    fun getSupportedDenoiseModes(parameters: Camera.Parameters?): List<String?>? {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no getSupportedDenoiseModes")
            return null
        }
        var supportedList: List<String?>? = null
        try {
            if (method_getSupportedDenoiseModes == null) {
                method_getSupportedDenoiseModes =
                    Camera.Parameters::class.java.getDeclaredMethod("getSupportedDenoiseModes")
            }
            supportedList = method_getSupportedDenoiseModes!!.invoke(parameters) as List<String?>
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return supportedList
    }

    private var method_setDenoise: Method? = null
    fun setDenoise(parameters: Camera.Parameters?, value: String?) {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no setDenoise")
            return
        }
        try {
            if (method_setDenoise == null) {
                method_setDenoise = Camera.Parameters::class.java.getDeclaredMethod(
                    "setDenoise",
                    String::class.java
                )
            }
            method_setDenoise!!.invoke(parameters, value)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private var method_getSupportedVideoHDRModes: Method? = null
    fun getSupportedVideoHDRModes(parameters: Camera.Parameters?): List<String?>? {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no getSupportedVideoHDRModes")
            return null
        }
        var supportedList: List<String?>? = null
        try {
            if (method_getSupportedVideoHDRModes == null) {
                method_getSupportedVideoHDRModes =
                    Camera.Parameters::class.java.getDeclaredMethod("getSupportedVideoHDRModes")
            }
            supportedList = method_getSupportedVideoHDRModes!!.invoke(parameters) as List<String?>
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return supportedList
    }

    private var method_getVideoHDRMode: Method? = null
    fun getVideoHDRMode(parameters: Camera.Parameters?): String? {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no getVideoHDRMode")
            return null
        }
        var hdrMode: String? = null
        try {
            if (method_getVideoHDRMode == null) {
                method_getVideoHDRMode =
                    Camera.Parameters::class.java.getDeclaredMethod("getVideoHDRMode")
            }
            hdrMode = method_getVideoHDRMode!!.invoke(parameters) as String
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return hdrMode
    }

    private var method_setVideoHDRMode: Method? = null
    fun setVideoHDRMode(parameters: Camera.Parameters?, videohdr: String?) {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no setVideoHDRMode")
            return
        }
        try {
            if (method_setVideoHDRMode == null) {
                method_setVideoHDRMode = Camera.Parameters::class.java.getDeclaredMethod(
                    "setVideoHDRMode",
                    String::class.java
                )
            }
            method_setVideoHDRMode!!.invoke(parameters, videohdr)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private var method_getSupportedHistogramModes: Method? = null
    fun getSupportedHistogramModes(parameters: Camera.Parameters?): List<String?>? {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no getSupportedHistogramModes")
            return null
        }
        var supportedList: List<String?>? = null
        try {
            if (method_getSupportedHistogramModes == null) {
                method_getSupportedHistogramModes =
                    Camera.Parameters::class.java.getDeclaredMethod("getSupportedHistogramModes")
            }
            supportedList = method_getSupportedHistogramModes!!.invoke(parameters) as List<String?>
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return supportedList
    }

    private var method_getSupportedHfrSizes: Method? = null
    fun getSupportedHfrSizes(parameters: Camera.Parameters?): List<Camera.Size?>? {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no getSupportedHfrSizes")
            return null
        }
        var supportedList: List<Camera.Size?>? = null
        try {
            if (method_getSupportedHfrSizes == null) {
                method_getSupportedHfrSizes =
                    Camera.Parameters::class.java.getDeclaredMethod("getSupportedHfrSizes")
            }
            supportedList = method_getSupportedHfrSizes!!.invoke(parameters) as List<Camera.Size?>
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return supportedList
    }

    private var method_getSupportedVideoHighFrameRateModes: Method? = null
    fun getSupportedVideoHighFrameRateModes(parameters: Camera.Parameters?): List<String?>? {
        if (DEBUG) {
            Log.e(
                TAG,
                "Debug:" + Camera.Parameters::class.java + " no getSupportedVideoHighFrameRateModes"
            )
            return null
        }
        var supportedList: List<String?>? = null
        try {
            if (method_getSupportedVideoHighFrameRateModes == null) {
                method_getSupportedVideoHighFrameRateModes =
                    Camera.Parameters::class.java.getDeclaredMethod("getSupportedVideoHighFrameRateModes")
            }
            supportedList =
                method_getSupportedVideoHighFrameRateModes!!.invoke(parameters) as List<String?>
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return supportedList
    }

    private var method_getVideoHighFrameRate: Method? = null
    fun getVideoHighFrameRate(parameters: Camera.Parameters?): String? {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no getVideoHighFrameRate")
            return null
        }
        var hfr: String? = null
        try {
            if (method_getVideoHighFrameRate == null) {
                method_getVideoHighFrameRate =
                    Camera.Parameters::class.java.getDeclaredMethod("getVideoHighFrameRate")
            }
            hfr = method_getVideoHighFrameRate!!.invoke(parameters) as String
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return hfr
    }

    private var method_setVideoHighFrameRate: Method? = null
    fun setVideoHighFrameRate(parameters: Camera.Parameters?, hfr: String?) {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no setVideoHighFrameRate")
            return
        }
        try {
            if (method_setVideoHighFrameRate == null) {
                method_setVideoHighFrameRate = Camera.Parameters::class.java.getDeclaredMethod(
                    "setVideoHighFrameRate", String::class.java
                )
            }
            method_setVideoHighFrameRate!!.invoke(parameters, hfr)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private var method_getSupportedVideoRotationValues: Method? = null
    fun getSupportedVideoRotationValues(parameters: Camera.Parameters?): List<String?>? {
        if (DEBUG) {
            Log.e(
                TAG,
                "Debug:" + Camera.Parameters::class.java + " no getSupportedVideoRotationValues"
            )
            return null
        }
        var supportedList: List<String?>? = null
        try {
            if (method_getSupportedVideoRotationValues == null) {
                method_getSupportedVideoRotationValues =
                    Camera.Parameters::class.java.getDeclaredMethod("getSupportedVideoRotationValues")
            }
            supportedList =
                method_getSupportedVideoRotationValues!!.invoke(parameters) as List<String?>
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return supportedList
    }

    private var method_setVideoRotation: Method? = null
    fun setVideoRotation(parameters: Camera.Parameters?, value: String?) {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no setVideoRotation")
            return
        }
        try {
            if (method_setVideoRotation == null) {
                method_setVideoRotation = Camera.Parameters::class.java.getDeclaredMethod(
                    "setVideoRotation", String::class.java
                )
            }
            method_setVideoRotation!!.invoke(parameters, value)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private var method_setFaceDetectionMode: Method? = null
    fun setFaceDetectionMode(parameters: Camera.Parameters?, value: String?) {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no setFaceDetectionMode")
            return
        }
        try {
            if (method_setFaceDetectionMode == null) {
                method_setFaceDetectionMode = Camera.Parameters::class.java.getDeclaredMethod(
                    "setFaceDetectionMode", String::class.java
                )
            }
            method_setFaceDetectionMode!!.invoke(parameters, value)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private var method_getSupportedFaceDetectionModes: Method? = null
    fun getSupportedFaceDetectionModes(parameters: Camera.Parameters?): List<String?>? {
        if (DEBUG) {
            Log.e(
                TAG,
                "Debug:" + Camera.Parameters::class.java + " no getSupportedFaceDetectionModes"
            )
            return null
        }
        var supportedList: List<String?>? = null
        try {
            if (method_getSupportedFaceDetectionModes == null) {
                method_getSupportedFaceDetectionModes =
                    Camera.Parameters::class.java.getDeclaredMethod("getSupportedFaceDetectionModes")
            }
            supportedList =
                method_getSupportedFaceDetectionModes!!.invoke(parameters) as List<String?>
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return supportedList
    }

    private var method_getSupportedZSLModes: Method? = null
    fun getSupportedZSLModes(parameters: Camera.Parameters?): List<String>? {
        var supportedList: List<String>? = null
        try {
            if (method_getSupportedZSLModes == null) {
                method_getSupportedZSLModes =
                    Camera.Parameters::class.java.getDeclaredMethod("getSupportedZSLModes")
            }
            supportedList = method_getSupportedZSLModes!!.invoke(parameters) as List<String>?
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return supportedList
    }

    private var method_setZSLMode: Method? = null
    fun setZSLMode(parameters: Camera.Parameters?, zsl: String?) {
        try {
            if (method_setZSLMode == null) {
                method_setZSLMode = Camera.Parameters::class.java.getDeclaredMethod(
                    "setZSLMode",
                    String::class.java
                )
            }
            method_setZSLMode!!.invoke(parameters, zsl)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private var method_getSharpness: Method? = null
    fun getSharpness(parameters: Camera.Parameters?): Int {
        var sharpness = -1
        try {
            if (method_getSharpness == null) {
                method_getSharpness =
                    Camera.Parameters::class.java.getDeclaredMethod("getSharpness")
            }
            sharpness = method_getSharpness!!.invoke(parameters) as Int
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return sharpness
    }

    private var method_setSharpness: Method? = null
    fun setSharpness(parameters: Camera.Parameters?, sharpness: Int) {
        try {
            if (method_setSharpness == null) {
                method_setSharpness = Camera.Parameters::class.java.getDeclaredMethod(
                    "setSharpness",
                    Int::class.javaPrimitiveType
                )
            }
            method_setSharpness!!.invoke(parameters, sharpness)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private var method_getMaxSharpness: Method? = null
    fun getMaxSharpness(parameters: Camera.Parameters?): Int {
        var maxSharpness = -1
        try {
            if (method_getMaxSharpness == null) {
                method_getMaxSharpness =
                    Camera.Parameters::class.java.getDeclaredMethod("getMaxSharpness")
            }
            maxSharpness = method_getMaxSharpness!!.invoke(parameters) as Int
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return maxSharpness
    }

    private var method_getSaturation: Method? = null
    fun getSaturation(parameters: Camera.Parameters?): Int {
        var saturation = -1
        try {
            if (method_getSaturation == null) {
                method_getSaturation =
                    Camera.Parameters::class.java.getDeclaredMethod("getSaturation")
            }
            saturation = method_getSaturation!!.invoke(parameters) as Int
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return saturation
    }

    private var method_setSaturation: Method? = null
    fun setSaturation(parameters: Camera.Parameters?, saturation: Int) {
        try {
            if (method_setSaturation == null) {
                method_setSaturation = Camera.Parameters::class.java.getDeclaredMethod(
                    "setSaturation",
                    Int::class.javaPrimitiveType
                )
            }
            method_setSaturation!!.invoke(parameters, saturation)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private var method_getMaxSaturation: Method? = null
    fun getMaxSaturation(parameters: Camera.Parameters?): Int {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no getMaxSaturation")
            return -1
        }
        var maxSaturation = -1
        try {
            if (method_getMaxSaturation == null) {
                method_getMaxSaturation =
                    Camera.Parameters::class.java.getDeclaredMethod("getMaxSaturation")
            }
            maxSaturation = method_getMaxSaturation!!.invoke(parameters) as Int
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return maxSaturation
    }

    private var method_getContrast: Method? = null
    fun getContrast(parameters: Camera.Parameters?): Int {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no getContrast")
            return -1
        }
        var contrast = -1
        try {
            if (method_getContrast == null) {
                method_getContrast = Camera.Parameters::class.java.getDeclaredMethod("getContrast")
            }
            contrast = method_getContrast!!.invoke(parameters) as Int
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return contrast
    }

    private var method_setContrast: Method? = null
    fun setContrast(parameters: Camera.Parameters?, contrast: Int) {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no setContrast")
            return
        }
        try {
            if (method_setContrast == null) {
                method_setContrast = Camera.Parameters::class.java.getDeclaredMethod(
                    "setContrast",
                    Int::class.javaPrimitiveType
                )
            }
            method_setContrast!!.invoke(parameters, contrast)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private var method_getMaxContrast: Method? = null
    fun getMaxContrast(parameters: Camera.Parameters?): Int {
        if (DEBUG) {
            Log.e(TAG, "Debug:" + Camera.Parameters::class.java + " no getMaxContrast")
            return -1
        }
        var maxContrast = -1
        try {
            if (method_getMaxContrast == null) {
                method_getMaxContrast =
                    Camera.Parameters::class.java.getDeclaredMethod("getMaxContrast")
            }
            maxContrast = method_getMaxContrast!!.invoke(parameters) as Int
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return maxContrast
    }
}
