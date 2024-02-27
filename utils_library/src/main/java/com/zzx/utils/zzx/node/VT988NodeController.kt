package com.zzx.utils.zzx.node

/**@author Tomy
 * Created by Tomy on 2024/2/26.
 */
class VT988NodeController: LedManualController(
    ledPath = LED_988_PATH,
    flashPath = FLASH_988,
    nightModePath = AUTO_NIGHT_MODE_988,
    nightModeSetPath = AUTO_NIGHT_MODE_SET_988,
    irLedPath = IR_LED_988,
    irCutPath = IR_CUT_988
) {

    companion object {
        /************ 200E 988 *************/
        const val LED_988_PATH  = "/sys/bus/platform/devices/soc:vtucomm-drv/breath_led"

        const val IR_LED_988    = "/sys/bus/platform/devices/soc:vtucomm-drv/ir_led"

        const val IR_CUT_988    = "/sys/bus/platform/devices/soc:vtucomm-drv/ir_cut"

        const val FLASH_988 = "/sys/bus/platform/devices/soc:vtucomm-drv/flash"

        //光敏电阻使能接口
        const val PHOTORESIST_EN_988   = "/sys/bus/platform/devices/soc:vtucomm-drv/photoresist_en"

        //光敏电阻电压查询接口
        const val PHOTORESIST_QUERY_988   = "/sys/bus/platform/devices/soc:vtucomm-drv/photoresist"

        //自动红外
        const val AUTO_NIGHT_MODE_988   = "/sys/bus/platform/devices/soc:vtucomm-drv/night_mode_test"

        //自动红外阙值设置
        const val AUTO_NIGHT_MODE_SET_988   = "/sys/bus/platform/devices/soc:vtucomm-drv/night_mode_test_theshold"
        /************ 200E 988 *************/
    }
}