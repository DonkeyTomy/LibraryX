package com.zzx.utils.zzx

import android.os.Build
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit

/**@author Tomy
 * Created by Tomy on 2022/5/14.
 */
class LedController private constructor() {

    private var mBreathDisposable: Disposable? = null

    private val mLedFile = File(LED_PATH)

    private val mFgLedGreen = File(FG_LED_NODE_GREEN)
    private val mFgLedRed   = File(FG_LED_NODE_RED)

    private val mBreath by lazy {
        Observable.interval(0,1000, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.io())
            .map {
                if (mBreathDisposable?.isDisposed == true) {
                    return@map
                }
                control(true, mColor)
            }.delay(500, TimeUnit.MILLISECONDS)
            .map {
                if (mBreathDisposable?.isDisposed == true) {
                    return@map
                }
                control(false, mColor)
            }
    }

    private var mColor = LED_GREEN

    fun takePic() {
        oneShot(LED_RED)
    }

    fun startRecordVideo() {
        control(LED_RED, true)
    }

    fun stopRecordVideo() {
        control(LED_GREEN, false)
    }

    fun startRecordVoice() {
        control(LED_YELLOW, true)
    }

    fun stopRecordVoice() {
        control(LED_GREEN, false)
    }

    fun control(color: Int, breath: Boolean = false) {
        synchronized(this) {
            if (breath) {
                mColor = color
                mBreathDisposable = mBreath.subscribe({}, { it.printStackTrace() })
            } else {
                mBreathDisposable?.dispose()
                Observable.just(color)
                    .observeOn(Schedulers.io())
                    .subscribe({
                        control(true, color)
                    }, {
                        it.printStackTrace()
                    })
            }
        }
    }

    fun oneShot(color: Int) {
        Observable.just(Unit)
            .observeOn(Schedulers.io())
            .map {
                control(true, color)
            }.delay(500, TimeUnit.MILLISECONDS)
            .map {
                control(true, LED_GREEN)
            }.subscribe({}, {
                it.printStackTrace()
            })
    }

    fun controlIrLed(isOpen: Boolean) {
        Timber.i("version = ${Build.DISPLAY}")
        if (isFg()) {
            ZZXMiscUtils.writeFile(FG_NOTE_IR_LED, if (isOpen) OPEN else LED_CLOSE)
        } else if (Build.DISPLAY.contains("230112") || Build.DISPLAY.contains("230218")) {
            ZZXMiscUtils.writeFile(NODE_PATH_IR_CUT_QCM, if (isOpen) LED_CLOSE else OPEN)
            Thread.sleep(500)
            ZZXMiscUtils.writeFile(NODE_PATH_IR_QCM, if (isOpen) OPEN else LED_CLOSE)
        } else {
            ZZXMiscUtils.writeFile(NODE_PATH_IR_CUT_QCM, if (isOpen) OPEN else LED_CLOSE)
        }
//        Thread.sleep(300)
//        ZZXMiscUtils.writeFile(NODE_PATH_IR_QCM, if (isOpen) OPEN else LED_CLOSE)
//        ZZXMiscUtils.write(NODE_PATH_IR_CUT_QCM, if (isOpen) OPEN else LED_CLOSE)
//        ZZXMiscUtils.write(NODE_PATH_IR_QCM, if (isOpen) OPEN else LED_CLOSE)
    }

    private fun isFg() = Build.MODEL.contains("VTU-A")

    private fun control(isOpen: Boolean, color: Int) {
        if (isFg()) {
            controlFgLed(isOpen, color)
            return
        }
        synchronized(this) {
            if (!isOpen) {
                mLedFile.writeText(LED_CLOSE)
            } else {
                when (color) {
                    LED_GREEN -> {
                        mLedFile.writeText(LED_OPEN_GREEN)
                    }
                    LED_RED -> {
                        mLedFile.writeText(LED_OPEN_RED)
                    }
                    LED_YELLOW -> {
                        mLedFile.writeText(LED_OPEN_YELLOW)
                    }
                }
            }
        }
    }

    private fun controlFgLed(isOpen: Boolean, color: Int) {
        synchronized(this) {
            if (!isOpen) {
                when (color) {
                    LED_GREEN -> {
                        mFgLedGreen.writeText(LED_CLOSE)
                    }
                    LED_RED -> {
                        mFgLedRed.writeText(LED_CLOSE)
                    }
                    LED_YELLOW -> {
                        mFgLedGreen.writeText(LED_CLOSE)
                        mFgLedRed.writeText(LED_CLOSE)
                    }
                }
            } else {
                when (color) {
                    LED_GREEN -> {
                        mFgLedRed.writeText(LED_CLOSE)
                        mFgLedGreen.writeText(OPEN)
                    }
                    LED_RED -> {
                        mFgLedGreen.writeText(LED_CLOSE)
                        mFgLedRed.writeText(OPEN)
                    }
                    LED_YELLOW -> {
                        mFgLedRed.writeText(OPEN)
                        mFgLedGreen.writeText(OPEN)
                    }
                }
            }
        }
    }


    companion object {
        val INSTANCE = LedController()

        const val LED_RED       = 0
        const val LED_GREEN     = 1
        const val LED_YELLOW    = 2

        const val LED_OPEN_RED  = "1"
        const val LED_OPEN_GREEN  = "2"
        const val LED_OPEN_BLUE  = "3"
        const val LED_OPEN_YELLOW  = "4"
        const val LED_CLOSE = "0"
        const val OPEN  = "1"

        const val LED_RED_PATH_MTK      = "/sys/devices/platform/soc/soc:leds/leds/i-red/brightness"
        const val LED_GREEN_PATH_MTK    = "/sys/devices/platform/soc/soc:leds/leds/i-green/brightness"

        const val LED_PATH      = "/sys/bus/platform/devices/soc:zzxcomm-drv/breath_led"

        const val NODE_PATH_IR_CUT_QCM = "/sys/bus/platform/devices/soc:qcom,ir-cut/ir_cut"
        const val NODE_PATH_IR_QCM = "/sys/bus/platform/devices/soc:xyc_lightsensor/ir_enable"

        /**
         * 适配方格节点
         */
        private const val FG_BASE_NODE_PATH_LED = "/sys/devices/platform/soc/soc:leds/leds"
        const val FG_LED_NODE_RED       = "$FG_BASE_NODE_PATH_LED/i-red/brightness"
        const val FG_LED_NODE_GREEN     = "$FG_BASE_NODE_PATH_LED/i-green/brightness"

        const val FG_NOTE_IR_LED    = "sys/devices/platform/soc/soc:qcom,ir-cut/ircut"
    }

}