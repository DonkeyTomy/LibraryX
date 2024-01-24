package com.tomy.compose.components.monitor

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import androidx.core.content.getSystemService
import com.tomy.compose.components.monitor.base.ItemState
import kotlinx.coroutines.channels.ProducerScope

/**@author Tomy
 * Created by Tomy on 2024/1/12.
 * @see mIconList 可用来存放蓝牙开关变化的图标，存放顺序为：0-关; 1-开; 2 - 已连接 .若无相应图标则存入-1，此时将发送[BluetoothAdapter]的标准状态[ON/OFF]
 */
class BluetoothMonitor(context: Context, iconArrayResId: Int = -1): StateOnlyBroadcastMonitor(context, iconArrayResId) {

    override val mAction: List<String>
        get() = arrayListOf(BluetoothAdapter.ACTION_STATE_CHANGED, BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)

        override suspend fun performAction(
            action: String,
            producerScope: ProducerScope<ItemState>,
            intent: Intent
        ) {
            assert(isIconListInit() && mIconList.size == 3)
            when (action) {
                BluetoothAdapter.ACTION_STATE_CHANGED   -> {
                    when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)) {
                        BluetoothAdapter.STATE_OFF  -> {
                            producerScope.send(ItemState.StatusOnly(convertState(BluetoothAdapter.STATE_OFF)))
                        }
                        BluetoothAdapter.STATE_ON   -> {
                            producerScope.send(ItemState.StatusOnly(convertState(BluetoothAdapter.STATE_ON)))
                        }
                    }
                }
                BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED    -> {
                    val connect = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, BluetoothAdapter.STATE_DISCONNECTED)
                    if (connect == BluetoothAdapter.STATE_CONNECTED) {
                        producerScope.send(ItemState.StatusOnly(convertState(BluetoothAdapter.STATE_CONNECTED)))
                    } else {
                        producerScope.send(ItemState.StatusOnly(convertState(BluetoothAdapter.STATE_ON)))
                    }
                }
            }
        }

    override fun convertState(originState: Int): Int {
        val icon = when (originState) {
            BluetoothAdapter.STATE_CONNECTED    -> {
                if (mIconList[2] > 0) {
                    mIconList[2]
                } else {
                    2
                }
            }

            BluetoothAdapter.STATE_ON   -> {
                if (mIconList[1] > 0) {
                    mIconList[1]
                } else {
                    1
                }
            }
            else  -> {
                if (mIconList[0] > 0) {
                    mIconList[0]
                } else {
                    0
                }
            }
        }
        return icon
    }

    @SuppressLint("MissingPermission")
    override suspend fun sendInitState(producerScope: ProducerScope<ItemState>) {
        val btAdapter = mContext.getSystemService<BluetoothManager>()!!.adapter
        val icon = if (btAdapter.isEnabled) {
            if (btAdapter.bondedDevices.isNotEmpty()) {
                convertState(BluetoothAdapter.STATE_CONNECTED)
            } else {
                convertState(BluetoothAdapter.STATE_ON)
            }
        } else {
            convertState(BluetoothAdapter.STATE_OFF)
        }
        producerScope.send(ItemState.StatusOnly(icon))
    }
}