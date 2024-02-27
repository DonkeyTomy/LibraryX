package com.zzx.utils.zzx

import android.os.Build
import com.zzx.utils.zzx.node.FgNodeController
import com.zzx.utils.zzx.node.INodeController
import com.zzx.utils.zzx.node.VT988NodeController
import com.zzx.utils.zzx.node.VtuNodeController

/**@author Tomy
 * Created by Tomy on 2022/5/14.
 */
class NodeController private constructor() {


    companion object {
        val INSTANCE: INodeController = if (Build.MODEL.contains(Regex("VTU-A|JY-G3"))) {
            FgNodeController()
        } else if (Build.MODEL.contains("VT988")) {
            VT988NodeController()
        } else {
            VtuNodeController()
        }

        const val LED_RED       = 0
        const val LED_GREEN     = 1
        const val LED_YELLOW    = 2

        const val LED_OPEN_RED  = "1"
        const val LED_OPEN_GREEN  = "2"
        const val LED_OPEN_BLUE  = "3"
        const val LED_OPEN_YELLOW  = "4"

        const val LED_RED_PATH_MTK      = "/sys/devices/platform/soc/soc:leds/leds/i-red/brightness"
        const val LED_GREEN_PATH_MTK    = "/sys/devices/platform/soc/soc:leds/leds/i-green/brightness"

        const val LED_PATH      = "/sys/bus/platform/devices/soc:zzxcomm-drv/breath_led"



        const val NODE_PATH_IR_CUT_QCM = "/sys/bus/platform/devices/soc:qcom,ir-cut/ir_cut"
        const val NODE_PATH_IR_QCM = "/sys/bus/platform/devices/soc:xyc_lightsensor/ir_enable"

    }

}