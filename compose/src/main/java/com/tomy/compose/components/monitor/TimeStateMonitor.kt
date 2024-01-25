package com.tomy.compose.components.monitor

import android.content.Context
import android.content.Intent
import com.tomy.compose.components.monitor.base.ABroadcastStateMonitor
import com.tomy.compose.components.monitor.base.ItemState
import kotlinx.coroutines.channels.ProducerScope
import timber.log.Timber

class TimeStateMonitor(context: Context): ABroadcastStateMonitor(context) {
    override val mAction: List<String>
        get() = arrayListOf(
            Intent.ACTION_TIME_TICK,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED
        )

    override suspend fun performAction(
        action: String,
        producerScope: ProducerScope<ItemState>,
        intent: Intent
    ) {
        Timber.d(action)
        producerScope.send(ItemState.StatusLong(System.currentTimeMillis()))
    }

    override suspend fun sendInitState(producerScope: ProducerScope<ItemState>) {
        producerScope.send(ItemState.StatusLong(System.currentTimeMillis()))
    }
}