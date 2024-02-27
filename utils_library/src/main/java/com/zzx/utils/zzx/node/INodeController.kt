package com.zzx.utils.zzx.node

/**@author Tomy
 * Created by Tomy on 2024/2/26.
 */
interface INodeController {

    fun takePic()

    fun startRecordVideo()

    fun stopRecordVideo()

    fun startRecordVoice()

    fun stopRecordVoice()

    fun control(color: Int, breath: Boolean = false)

    fun oneShot(color: Int)

    fun controlIrLed(isOpen: Boolean)

    fun control(isOpen: Boolean, color: Int)

    fun controlFlash(isOpen: Boolean)

    fun setNightModeEnable(enabled: Boolean)

    fun setNightModeThreshold(threshold: Int)

    fun getNightModeThreshold(): String

    companion object {
        const val LED_CLOSE = "0"
        const val OPEN  = "1"
    }

}