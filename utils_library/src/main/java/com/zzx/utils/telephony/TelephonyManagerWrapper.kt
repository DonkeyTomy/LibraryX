package com.zzx.utils.telephony

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.core.content.getSystemService

@SuppressLint("DiscouragedPrivateApi")
class TelephonyManagerWrapper(context: Context) {

    val mTelephonyManager = context.getSystemService<TelephonyManager>()!!

    private val mSetDataEnableMethod by lazy {
        TelephonyManager::class.java.getDeclaredMethod("setDataEnabled", Integer.TYPE, Boolean::class.java)
    }

    private val mIsDataEnabledMethod by lazy {
        TelephonyManager::class.java.getDeclaredMethod("getDataEnabled", Integer.TYPE)
    }

    private val mIsMultiSimEnabled by lazy {
        TelephonyManager::class.java.getDeclaredMethod("isMultiSimEnabled")
    }

    fun setDataEnabled(subId: Int = SubscriptionManager.getDefaultDataSubscriptionId(), enable: Boolean) {
        try {
            mSetDataEnableMethod.invoke(mTelephonyManager, subId, enable)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isDataEnabled(subId: Int = SubscriptionManager.getDefaultDataSubscriptionId()): Boolean {
        return try {
            mIsDataEnabledMethod.invoke(mTelephonyManager, subId) as Boolean
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun isMultiSimEnabled(): Boolean {
        return try {
            mIsMultiSimEnabled.invoke(mTelephonyManager) as  Boolean
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    @SuppressLint("MissingPermission")
    fun getImei(index: Int): String {
        val imei = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mTelephonyManager.getImei(index)
            } else {
                TelephonyManager::class.java.getDeclaredMethod("getImei", Int::class.java).invoke(mTelephonyManager, index) as String
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
        return imei ?: ""
    }

}