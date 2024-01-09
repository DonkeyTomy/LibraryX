package com.tomy.compose.components.monitor

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.channels.ProducerScope

/**@author Tomy
 * Created by Tomy on 2024/1/9.
 */
class StateOnlyBroadcastMonitor(context: Context): BroadcastStateMonitor(context) {

    override val mAction: List<String>
        get() = emptyList()

    override suspend fun performAction(
        index: Int,
        producerScope: ProducerScope<ItemState.StatusOnly>,
        intent: Intent
    ) {
        producerScope.send(ItemState.StatusOnly(0))
    }

}