package com.zzx.utils.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

/**@author Tomy
 * Created by Tomy on 2023/3/6.
 */
class NetworkObserver: IConnectivityObserver {
    override fun observer(): Flow<IConnectivityObserver.Status> {
        return callbackFlow { 
            val wifiScanReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent) {
                    launch {
                    }
                }
            }
        }
    }
}