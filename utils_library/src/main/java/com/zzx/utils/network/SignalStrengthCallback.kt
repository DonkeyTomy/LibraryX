package com.zzx.utils.network

import android.telephony.ServiceState
import android.telephony.SignalStrength

/**@author Tomy
 * Created by Tomy on 2024/1/24.
 */
interface SignalStrengthCallback {

    fun onSignalStrengthsChanged(signalStrength: SignalStrength)

    fun onServiceStateChanged(serviceState: ServiceState)
}