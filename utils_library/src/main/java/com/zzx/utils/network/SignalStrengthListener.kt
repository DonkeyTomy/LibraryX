package com.zzx.utils.network

import android.content.Context
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.ServiceState
import android.telephony.SignalStrength
import android.telephony.SubscriptionManager
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import java.util.concurrent.Executors

/**@author Tomy
 * Created by Tomy on 2024/1/24.
 */
class SignalStrengthListener(context: Context, subId: Int = SubscriptionManager.getDefaultSubscriptionId()) {

    private val telephonyManager = context.getSystemService<TelephonyManager>()?.createForSubscriptionId(subId)

    private lateinit var phoneStateListener: PhoneStateListener

    private lateinit var telephonyCallback: TelephonyCallback

    private var mSignalStrengthCallback: SignalStrengthCallback? = null

    fun setSignalStrengthCallback(callback: SignalStrengthCallback?) {
        mSignalStrengthCallback = callback
    }

    fun startListen(callback: SignalStrengthCallback? = null) {
        if (callback != null) {
            setSignalStrengthCallback(callback)
        }
        assert(mSignalStrengthCallback != null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            @RequiresApi(Build.VERSION_CODES.S)
            class SignalCallback: TelephonyCallback(), TelephonyCallback.SignalStrengthsListener,
                TelephonyCallback.ServiceStateListener {
                override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                    mSignalStrengthCallback?.onSignalStrengthsChanged(signalStrength)
                }

                override fun onServiceStateChanged(serviceState: ServiceState) {
                    mSignalStrengthCallback?.onServiceStateChanged(serviceState)
                }

            }
            telephonyCallback = SignalCallback()

            telephonyManager?.registerTelephonyCallback(Executors.newSingleThreadExecutor(), telephonyCallback)
        } else {
            phoneStateListener = object : PhoneStateListener() {

                override fun onServiceStateChanged(serviceState: ServiceState) {
                    mSignalStrengthCallback?.onServiceStateChanged(serviceState)
                }

                override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                    mSignalStrengthCallback?.onSignalStrengthsChanged(signalStrength)
                }

                override fun onSignalStrengthChanged(asu: Int) {
                    super.onSignalStrengthChanged(asu)
                }

            }
            telephonyManager?.listen(phoneStateListener, PhoneStateListener.LISTEN_SERVICE_STATE.or(PhoneStateListener.LISTEN_SIGNAL_STRENGTH))
        }
    }

    fun stopListen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyManager?.unregisterTelephonyCallback(telephonyCallback)
        } else {
            telephonyManager?.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        }
    }

}