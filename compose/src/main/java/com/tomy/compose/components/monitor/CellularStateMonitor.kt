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

/**@author Tomy
 * Created by Tomy on 2024/1/24.
 */
class CellularStateMonitor(context: Context, subId: Int = SubscriptionManager.getDefaultSubscriptionId()): IMonitor {

    private val mSignalStrengthListener = SignalStrengthListener(context, subId)

    lateinit var mIconList: List<Int>

    fun isIconListInit() = ::mIconList.isInitialized

    fun setIconList(iconList: List<Int>) {
        mIconList = iconList
    }

    open fun convertState(originState: Int) = 0

    @SuppressLint("MissingPermission")
    override suspend fun startMonitor(producerScope: ProducerScope<ItemState>) {
        val signalCallback = object : SignalStrengthCallback {
            override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
            }

            override fun onServiceStateChanged(serviceState: ServiceState) {
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