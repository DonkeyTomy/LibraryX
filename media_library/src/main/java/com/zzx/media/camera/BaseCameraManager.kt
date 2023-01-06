package com.zzx.media.camera

import android.hardware.Camera.Size
import android.hardware.camera2.params.Face

/**@author Tomy
 * Created by Tomy on 2022/7/20.
 */
abstract class BaseCameraManager<surface, camera>: ICameraManager<surface, camera> {


    protected var supportedFacing = HashSet<Face>(2)
    protected var supportedPictureSizes = ArrayList<Size>(15)
    protected var supportedVideoSizes = ArrayList<Size>(5)
    protected var supportedPictureAspectRatio: Set<AspectRatio> = HashSet<AspectRatio>(4)
    protected var supportedVideoAspectRatio: Set<AspectRatio> = HashSet<AspectRatio>(3)
    protected var supportedFrameProcessingFormats: Set<Int> = HashSet(2)

    protected var zoomSupported = false
    protected var exposureCorrectionSupported = false
    protected var exposureCorrectionMinValue = 0f
    protected var exposureCorrectionMaxValue = 0f
    protected var autoFocusSupported = false
    protected var previewFrameRateMinValue = 0f
    protected var previewFrameRateMaxValue = 0f

    protected var burstModeSupported = false

    protected var burstModeMaxCount = 0



}