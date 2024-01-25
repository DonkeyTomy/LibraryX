package com.tomy.compose.components.monitor

import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import androidx.core.content.getSystemService
import com.tomy.compose.components.monitor.base.ABroadcastStateMonitor
import com.tomy.compose.components.monitor.base.ItemState
import kotlinx.coroutines.channels.ProducerScope
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2024/1/25.
 */
class BatteryStateMonitor(context: Context): ABroadcastStateMonitor(context) {

    private var mCharging = DISCHARGING

    private var mCapacity = 0

    override val mAction: List<String>
        get() = arrayListOf(
            BatteryManager.ACTION_CHARGING,
            Intent.ACTION_BATTERY_CHANGED,
            BatteryManager.ACTION_DISCHARGING
        )

    override suspend fun performAction(
        action: String,
        producerScope: ProducerScope<ItemState>,
        intent: Intent
    ) {
        when (action) {
            BatteryManager.ACTION_CHARGING  -> {
                Timber.v("action: $action")
                mCharging = CHARGING
            }
            BatteryManager.ACTION_DISCHARGING   -> {
                Timber.v("action: $action")
                mCharging = DISCHARGING
            }
            Intent.ACTION_BATTERY_CHANGED   -> {
                mCapacity = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            }
        }
        producerScope.send(ItemState.StatusOnly(mCharging.or(mCapacity)))

    }

    override suspend fun sendInitState(producerScope: ProducerScope<ItemState>) {
        mContext.getSystemService<BatteryManager>()?.apply {
            mCharging = if (isCharging) CHARGING else DISCHARGING
            val capacity = getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            Timber.v("charging: $mCharging; capacity: $capacity; level: $mCapacity")
            producerScope.send(ItemState.StatusOnly(mCharging.or(capacity)))
        }
    }




    companion object {
        const val CHARGING   = 0x100
        const val DISCHARGING   = 0x000

    }

}