package com.tomy.compose.components.monitor

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.ServiceState
import android.telephony.SignalStrength
import android.telephony.SubscriptionManager
import com.tomy.compose.components.monitor.base.IMonitor
import com.tomy.compose.components.monitor.base.ItemState
import com.zzx.utils.network.SignalStrengthCallback
import com.zzx.utils.network.SignalStrengthListener
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.launch
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2024/1/24.
 */
class CellularStateMonitor(context: Context, subId: Int = SubscriptionManager.getDefaultSubscriptionId()): IMonitor {

    private val mSignalStrengthListener = SignalStrengthListener(context, subId)

    lateinit var mIconList: List<Int>


    fun setIconList(iconList: List<Int>) {
        mIconList = iconList
    }


    @SuppressLint("MissingPermission")
    override suspend fun startMonitor(producerScope: ProducerScope<ItemState>) {
        val signalCallback = object : SignalStrengthCallback {
            override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                Timber.v("onSignalStrengthsChanged: ${signalStrength.level}")
                producerScope.launch {
                    producerScope.send(ItemState.StatusOnly(mIconList[signalStrength.level]))
                }
            }

            override fun onServiceStateChanged(serviceState: ServiceState) {
//                Timber.v("${serviceState}; ${serviceState.operatorAlphaShort}; ${serviceState.operatorNumeric}")
                when (serviceState.state) {
                    ServiceState.STATE_IN_SERVICE   -> {
                        Timber.d("STATE_IN_SERVICE")
                    }
                    ServiceState.STATE_OUT_OF_SERVICE,
                    ServiceState.STATE_POWER_OFF    -> {
                        Timber.e("STATE_OUT_OF_SERVICE")
                    }
                    ServiceState.STATE_EMERGENCY_ONLY   -> {
                        Timber.w("STATE_EMERGENCY_ONLY")
                    }
                }
            }
        }

        mSignalStrengthListener.startListen(signalCallback)

    }

    override fun stopMonitor() {
        mSignalStrengthListener.stopListen()
    }

    override suspend fun sendInitState(producerScope: ProducerScope<ItemState>) {
    }

}