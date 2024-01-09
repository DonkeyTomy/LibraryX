package com.tomy.compose.components.monitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**@author Tomy
 * Created by Tomy on 2024/1/9.
 */
abstract class BroadcastStateMonitor(val mContext: Context): AStateMonitor() {

    abstract val mAction: List<String>

    override suspend fun startMonitor() = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val action = intent.action
                launch {
                    mAction.forEachIndexed { index, act ->
                        if (action == act) {
                            performAction(index, this@callbackFlow, intent)
                        }
                    }
                }
            }
        }

        mContext.registerReceiver(receiver, IntentFilter().apply {
            mAction.forEach {
                addAction(it)
            }
        })
        awaitClose {
            mContext.unregisterReceiver(receiver)
        }
    }.distinctUntilChanged()

    abstract suspend fun performAction(index: Int, producerScope: ProducerScope<ItemState.StatusOnly>, intent: Intent)

}