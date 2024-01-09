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
class BroadcastMonitor(val mContext: Context): IMonitor {

    lateinit var mReceiver: BroadcastReceiver

    override suspend fun startMonitor(producerScope: ProducerScope<ItemState.StatusOnly>) {
        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                producerScope.launch {
                    producerScope.send(ItemState.StatusOnly(0))
                }
            }

        }
        mContext.registerReceiver(mReceiver, IntentFilter())
    }

    override suspend fun stopMonitor() {
        mContext.unregisterReceiver(mReceiver)
    }
}