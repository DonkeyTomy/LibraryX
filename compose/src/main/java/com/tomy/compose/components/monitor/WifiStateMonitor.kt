package com.tomy.compose.components.monitor

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.os.Build
import androidx.core.content.getSystemService
import com.tomy.compose.components.monitor.base.ItemState
import com.tomy.compose.components.monitor.StateOnlyBroadcastMonitor
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.launch
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2024/1/11.
 * @see mIconList 可用来存放Wifi开关变化的图标，存放顺序为：0 - 关,1 -开.若关默认隐藏则只需放开的图标，关闭状态则发送-1.如果都没放则发送[WifiManager]的标准状态[DISABLE/ENABLED]
 */
class WifiStateMonitor(context: Context): StateOnlyBroadcastMonitor(context) {

    private val mWifiManager by lazy { mContext.getSystemService<WifiManager>() }
    private val mConnectivityManager = context.getSystemService<ConnectivityManager>()!!

    private lateinit var networkCallback: NetworkCallback

    private var mSignalLevel = -1

    override val mAction: List<String>
        get() = arrayListOf(WifiManager.WIFI_STATE_CHANGED_ACTION)

    override suspend fun performAction(
        action: String,
        producerScope: ProducerScope<ItemState>,
        intent: Intent
    ) {
        when (action) {
            WifiManager.WIFI_STATE_CHANGED_ACTION   -> {
                when (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED)) {
                    WifiManager.WIFI_STATE_DISABLED -> {
                        Timber.d("disabled")
                        mSignalLevel = -1
                        producerScope.send(ItemState.StatusOnly(-1))
                    }
                    WifiManager.WIFI_STATE_ENABLED  -> {
                        Timber.d("enabled: $mSignalLevel")
                        if (mSignalLevel <= 0) {
                            producerScope.send(ItemState.StatusOnly(mIconList[0]))
                        }
                    }
                }
            }
            WifiManager.RSSI_CHANGED_ACTION -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    mWifiManager!!.calculateSignalLevel(mWifiManager?.connectionInfo!!.rssi)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun startMonitor(producerScope: ProducerScope<ItemState>) {
        super.startMonitor(producerScope)
        networkCallback = object : NetworkCallback() {

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val signal = mWifiManager!!.calculateSignalLevel(networkCapabilities.signalStrength)
                    Timber.d("wifiSignal: ${networkCapabilities.signalStrength}: $signal")
                    mSignalLevel = signal
                    producerScope.launch {
                        producerScope.send(ItemState.StatusOnly(mIconList[signal]))
                    }

                }
            }

            override fun onLost(network: Network) {
                if (mWifiManager!!.isWifiEnabled) {
                    mSignalLevel = -1
                    Timber.d("onLost")
                    producerScope.launch {
                        producerScope.send(ItemState.StatusOnly(mIconList[0]))
                    }
                }
            }

        }
        mConnectivityManager.registerNetworkCallback(
            NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build(),
            networkCallback
        )
    }

    override fun stopMonitor() {
        super.stopMonitor()
        mConnectivityManager.unregisterNetworkCallback(networkCallback)
    }

    override suspend fun sendInitState(producerScope: ProducerScope<ItemState>) {
        /*mWifiManager?.apply {
            if (isWifiEnabled) {
                if (connectionInfo != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val level = calculateSignalLevel(connectionInfo.rssi)
                        mSignalLevel = level
                        Timber.v("sendInitState: $level")
                        producerScope.send(ItemState.StatusOnly(mIconList[level]))
                    }
                } else {
                    mSignalLevel = 0
                    Timber.v("sendInitState: 0")
                    producerScope.send(ItemState.StatusOnly(mIconList[0]))
                }
            } else {
                mSignalLevel = -1
                producerScope.send(ItemState.StatusOnly(-1))
            }
        }*/
    }

}