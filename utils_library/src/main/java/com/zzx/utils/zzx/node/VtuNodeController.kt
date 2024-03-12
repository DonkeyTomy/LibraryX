package com.zzx.utils.zzx.node

import android.os.Build
import com.zzx.utils.zzx.ZZXMiscUtils
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2024/2/26.
 */
class VtuNodeController: LedManualController(
    ledPath = LED_PATH,
    irLedPath = NODE_PATH_IR_QCM,
    irCutPath = NODE_PATH_IR_CUT_QCM,
    nightModePath = AUTO_NIGHT_MODE_VTU,
    nightModeSetPath = AUTO_NIGHT_MODE_SET_VTU
) {

    override fun controlIrLed(isOpen: Boolean) {
        Timber.i("version = ${Build.DISPLAY}; model: ${Build.MODEL}; ID: ${Build.ID}")
        if (Build.DISPLAY.contains("230112") || Build.DISPLAY.contains("230218")) {
            ZZXMiscUtils.writeFile(NODE_PATH_IR_CUT_QCM, if (isOpen) INodeController.LED_CLOSE else INodeController.OPEN)
            Thread.sleep(500)
            ZZXMiscUtils.writeFile(NODE_PATH_IR_QCM, if (isOpen) INodeController.OPEN else INodeController.LED_CLOSE)
        } else {
            ZZXMiscUtils.writeFile(NODE_PATH_IR_CUT_QCM, if (isOpen) INodeController.OPEN else INodeController.LED_CLOSE)
        }
    }

    companion object {
        const val LED_PATH      = "/sys/bus/platform/devices/soc:zzxcomm-drv/breath_led"



        const val NODE_PATH_IR_CUT_QCM = "/sys/bus/platform/devices/soc:qcom,ir-cut/ir_cut"
        const val NODE_PATH_IR_QCM = "/sys/bus/platform/devices/soc:xyc_lightsensor/ir_enable"

        const val AUTO_NIGHT_MODE_VTU  = "/sys/bus/platform/devices/soc:qcom,ir-cut/night_mode_test"
        const val AUTO_NIGHT_MODE_SET_VTU   = "/sys/bus/platform/devices/soc:xyc_lightsensor/ir_threshold"
    }
}