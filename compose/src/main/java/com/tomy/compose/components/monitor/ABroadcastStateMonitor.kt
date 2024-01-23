package com.tomy.compose.components.monitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.launch

/**@author Tomy
 * Created by Tomy on 2024/1/9.
 */
abstract class ABroadcastStateMonitor(val mContext: Context): IMonitor {

    abstract val mAction: List<String>
    private lateinit var mReceiver: BroadcastReceiver

    override suspend fun startMonitor(producerScope: ProducerScope<ItemState>) {
        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val action = intent.action!!
                producerScope.launch {
                    performAction(action, producerScope, intent)
                }
            }
        }

        mContext.registerReceiver(mReceiver, IntentFilter().apply {
            mAction.forEach {
                addAction(it)
            }
        })

    }

    override fun stopMonitor() {
        mContext.unregisterReceiver(mReceiver)
    }

    abstract suspend fun performAction(action: String, producerScope: ProducerScope<ItemState>, intent: Intent)

}