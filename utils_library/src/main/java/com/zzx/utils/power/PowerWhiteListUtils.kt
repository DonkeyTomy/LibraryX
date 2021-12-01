package com.zzx.utils.power

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.IBinder
import android.os.IDeviceIdleController
import android.os.PowerManager
import androidx.annotation.RequiresApi
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2021/11/9.
 */
class PowerWhiteListUtils private constructor() {

    fun addToWhiteList(context: Context, pkgName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            addToWhitelist(context, arrayListOf(pkgName))
        } else {
            addToPowerSaveList(pkgName)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addToWhitelist(context: Context, list: List<String>) {
        try {
            val powerManager = context.getSystemService(PowerManager::class.java)
            val method = PowerManager::class.java.getMethod("getPowerWhitelistManager")
            val powerWhiteListManager = method.invoke(powerManager)
            Timber.v("powerWhiteListManager = $powerWhiteListManager")
            val addListMethod = powerWhiteListManager.javaClass.getMethod("addToWhitelist", List::class.java)
            addListMethod.invoke(powerWhiteListManager, list)
            Timber.v("addListMethod = $addListMethod")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    fun addToPowerSaveList(pkgName: String) {
        try {
            val serviceManager = Class.forName("android.os.ServiceManager")
            val method = serviceManager.getDeclaredMethod("getService", String::class.java)
            val powerWhiteListManager = IDeviceIdleController.Stub.asInterface(method.invoke(null, "deviceidle") as? IBinder)
            Timber.d("powerWhiteListManager = $powerWhiteListManager")
            powerWhiteListManager?.addPowerSaveWhitelistApp(pkgName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private var instance: PowerWhiteListUtils? = null
        val INSTANCE: PowerWhiteListUtils get() {
            if (instance == null) {
                instance = PowerWhiteListUtils()
            }
            return instance!!
        }

        private const val POWER_WHITELIST_MANAGER = "power_whitelist"
    }

}