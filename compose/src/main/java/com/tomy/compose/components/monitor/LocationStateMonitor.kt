package com.tomy.compose.components.monitor

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import androidx.core.content.getSystemService
import androidx.core.location.LocationManagerCompat
import com.tomy.compose.components.monitor.base.ItemState
import kotlinx.coroutines.channels.ProducerScope
import timber.log.Timber

/**
 * @see mIconList 0: 正在定位; 1: 已定位(若无可不存放)
 */
class LocationStateMonitor(context: Context): StateOnlyBroadcastMonitor(context) {

    private val mLocationManager: LocationManager by lazy {
        mContext.getSystemService<LocationManager>()!!
    }

    override val mAction: List<String>
        get() = arrayListOf(LocationManager.MODE_CHANGED_ACTION)

    override suspend fun performAction(
        action: String,
        producerScope: ProducerScope<ItemState>,
        intent: Intent
    ) {
        val enable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            intent.getBooleanExtra(LocationManager.EXTRA_LOCATION_ENABLED, false)
        } else {
            LocationManagerCompat.isLocationEnabled(mLocationManager)
        }
        Timber.i("locationChanged: $enable")
        val index = if (enable) {
            mIconList[0]
        } else {
            -1
        }
        producerScope.send(ItemState.StatusOnly(index))
    }

    override suspend fun sendInitState(producerScope: ProducerScope<ItemState>) {
        val index = if (LocationManagerCompat.isLocationEnabled(mLocationManager)) {
            mIconList[0]
        } else {
            -1
        }
        producerScope.send(ItemState.StatusOnly(index))
    }

}