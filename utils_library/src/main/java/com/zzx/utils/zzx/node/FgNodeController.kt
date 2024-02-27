package com.zzx.utils.zzx.node

import com.zzx.utils.zzx.ZZXMiscUtils
import java.io.File


/**@author Tomy
 * Created by Tomy on 2024/2/26.
 */
class FgNodeController: LedManualController(
    ledPath = "",
    irLedPath = FG_NOTE_IR_LED,
    irCutPath = ""
) {

    private val mFgLedGreen = File(FG_LED_NODE_GREEN)
    private val mFgLedRed   = File(FG_LED_NODE_RED)

    override fun controlIrLed(isOpen: Boolean) {
        ZZXMiscUtils.writeFile(irLedPath, isOpen)
    }

    override fun control(isOpen: Boolean, color: Int) {
        synchronized(this) {
            if (!isOpen) {
                when (color) {
                    ledGreen -> {
                        mFgLedGreen.writeText(INodeController.LED_CLOSE)
                    }
                    ledRed -> {
                        mFgLedRed.writeText(INodeController.LED_CLOSE)
                    }
                    ledYellow -> {
                        mFgLedGreen.writeText(INodeController.LED_CLOSE)
                        mFgLedRed.writeText(INodeController.LED_CLOSE)
                    }
                }
            } else {
                when (color) {
                    ledGreen -> {
                        mFgLedRed.writeText(INodeController.LED_CLOSE)
                        mFgLedGreen.writeText(INodeController.OPEN)
                    }
                    ledRed -> {
                        mFgLedGreen.writeText(INodeController.LED_CLOSE)
                        mFgLedRed.writeText(INodeController.OPEN)
                    }
                    ledYellow -> {
                        mFgLedRed.writeText(INodeController.OPEN)
                        mFgLedGreen.writeText(INodeController.OPEN)
                    }
                }
            }
        }
    }

    companion object {
        private const val FG_BASE_NODE_PATH_LED = "/sys/devices/platform/soc/soc:leds/leds"
        const val FG_LED_NODE_RED       = "${FG_BASE_NODE_PATH_LED}/i-red/brightness"
        const val FG_LED_NODE_GREEN     = "${FG_BASE_NODE_PATH_LED}/i-green/brightness"

        const val FG_NOTE_IR_LED    = "sys/devices/platform/soc/soc:qcom,ir-cut/ircut"
    }
}