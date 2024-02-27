package com.zzx.utils.zzx.node

import com.zzx.utils.zzx.ZZXMiscUtils
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import java.util.concurrent.TimeUnit

/**@author Tomy
 * Created by Tomy on 2024/2/26.
 */
abstract class LedManualController(
    ledPath: String,
    val irLedPath: String,
    val irCutPath: String,
    val flashPath: String = "",
    val nightModePath: String = "",
    val nightModeSetPath: String = "",
    val ledRed: Int = 1,
    val ledGreen: Int = 2,
    val ledBlue: Int = 3,
    val ledYellow: Int = 4
): INodeController {

    private var mBreathDisposable: Disposable? = null

    private val mLedFile = File(ledPath)

    private var mColor = ledRed


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


    override fun takePic() {
        oneShot(ledRed)
    }

    override fun startRecordVideo() {
        control(ledRed, true)
    }

    override fun stopRecordVideo() {
        control(ledGreen, false)
    }

    override fun startRecordVoice() {
        control(ledYellow, true)
    }

    override fun stopRecordVoice() {
        control(ledGreen, false)
    }

    override fun control(color: Int, breath: Boolean) {
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

    override fun control(isOpen: Boolean, color: Int) {
        synchronized(this) {
            if (!isOpen) {
                mLedFile.writeText(INodeController.LED_CLOSE)
            } else {
                mLedFile.writeText("$color")
            }
        }
    }

    override fun oneShot(color: Int) {
        Observable.just(Unit)
            .observeOn(Schedulers.io())
            .map {
                control(true, color)
            }.delay(500, TimeUnit.MILLISECONDS)
            .map {
                control(true, ledGreen)
            }.subscribe({}, {
                it.printStackTrace()
            })
    }

    override fun controlIrLed(isOpen: Boolean) {
        ZZXMiscUtils.writeFile(irCutPath, if (isOpen) INodeController.OPEN else INodeController.LED_CLOSE)
        Thread.sleep(500)
        ZZXMiscUtils.writeFile(irLedPath, if (isOpen) INodeController.OPEN else INodeController.LED_CLOSE)
    }

    override fun controlFlash(isOpen: Boolean) {
        if (flashPath.isNotEmpty()) {
            ZZXMiscUtils.writeFile(flashPath, isOpen)
        }
    }

    override fun setNightModeEnable(enabled: Boolean) {
        if (nightModePath.isNotEmpty())
            ZZXMiscUtils.writeFile(nightModePath, enabled)
    }

    override fun setNightModeThreshold(threshold: Int) {
        if (nightModeSetPath.isNotEmpty())
            ZZXMiscUtils.writeFile(nightModeSetPath, "$threshold")
    }

    override fun getNightModeThreshold(): String {
        if (nightModeSetPath.isEmpty())
            return ""
        return ZZXMiscUtils.read(nightModeSetPath)
    }

}