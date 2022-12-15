package com.zzx.utils.power

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.os.PowerManager
import android.os.SystemClock
import java.util.concurrent.atomic.AtomicBoolean
import androidx.core.content.getSystemService
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**@author Tomy
 * Created by Tomy on 2019/9/25.
 */
class WakeLockUtil(context: Context) {

    private val mLockAcquire = AtomicBoolean(false)

    private val mPm by lazy {
        context.getSystemService(PowerManager::class.java)
    }
    private val mKeyguardManager by lazy { context.getSystemService<KeyguardManager>()!! }

    private val mKeyguardLock: KeyguardManager.KeyguardLock by lazy {
        mKeyguardManager.newKeyguardLock(WakeLockUtil::class.simpleName)
    }

    private val mScreenOffMethod by lazy {
        PowerManager::class.java.getDeclaredMethod("goToSleep", Long::class.java)
    }

    private val mScreenOnMethod by lazy {
        PowerManager::class.java.getDeclaredMethod("wakeUp", Long::class.java, String::class.java)
    }

    @SuppressLint("MissingPermission")
    fun unlockScreen() {
        if (mKeyguardManager.isKeyguardLocked) {
            mKeyguardLock.disableKeyguard()
        }
    }


    private val mWakeLock by lazy {
        mPm?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK.or(PowerManager.ACQUIRE_CAUSES_WAKEUP), "cpuLock")
    }

    fun lock() {
        if (mLockAcquire.get()) {
            return
        }
        mWakeLock?.acquire()
        mLockAcquire.set(true)
    }

    fun releaseLock() {
        if (!mLockAcquire.get()) {
            return
        }
        mWakeLock?.release()
        mLockAcquire.set(false)
    }

    fun screenOn() {
        try {
            if (!mPm.isScreenOn) {
                Timber.d("wakeUp screenOn")
                mScreenOnMethod.invoke(mPm, SystemClock.uptimeMillis(), "WAKE_REASON_CAMERA_LAUNCH")
                Observable.just(Unit)
                    .delay(800, TimeUnit.MILLISECONDS)
                    .subscribe {
                        screenOff()
                    }
            }
            unlockScreen()
//            lock()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun screenOff() {
        try {
            if (mPm.isScreenOn) {
                mScreenOffMethod.invoke(mPm, SystemClock.uptimeMillis())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}