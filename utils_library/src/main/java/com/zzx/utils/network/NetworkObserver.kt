package com.zzx.utils.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.os.Build
import androidx.core.content.getSystemService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2023/3/6.
 */
class NetworkObserver(context: Context, val networkType: Int): IConnectivityObserver {

    private val mConnectivityManager = context.getSystemService<ConnectivityManager>()!!

    private val mWifiManager = context.getSystemService<WifiManager>()

    @SuppressLint("MissingPermission")
    override fun observer(): Flow<IConnectivityObserver.Status> {
        return callbackFlow {
            val networkCallback = object : NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    trySend(IConnectivityObserver.Status.Available)
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

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val signal = mWifiManager!!.calculateSignalLevel(networkCapabilities.signalStrength)
                        Timber.d("wifiSignal: ${networkCapabilities.signalStrength}: $signal")
                    }
                    super.onCapabilitiesChanged(network, networkCapabilities)
                }
            }
            mConnectivityManager.registerNetworkCallback(
                NetworkRequest.Builder()
                    .addTransportType(networkType)
//                    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                    .build(),
                networkCallback
            )
            awaitClose {
                mConnectivityManager.unregisterNetworkCallback(networkCallback)
            }
        }
    }
}