package com.zzx.utils.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkRequest
import androidx.core.content.getSystemService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**@author Tomy
 * Created by Tomy on 2023/3/6.
 */
class NetworkObserver(context: Context): IConnectivityObserver {

    private val mConnectivityManager = context.getSystemService<ConnectivityManager>()!!

    @SuppressLint("MissingPermission")
    override fun observer(): Flow<IConnectivityObserver.Status> {
        return callbackFlow {
            val networkCallback = object : NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    trySend(IConnectivityObserver.Status.Available)
                    close()
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    trySend(IConnectivityObserver.Status.Unavailable)
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    trySend(IConnectivityObserver.Status.Losing)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    trySend(IConnectivityObserver.Status.Lost)
                }
            }
            mConnectivityManager.registerNetworkCallback(
                NetworkRequest.Builder()
                    .build(),
                networkCallback
            )
            awaitClose {
                mConnectivityManager.unregisterNetworkCallback(networkCallback)
            }
        }
    }
}