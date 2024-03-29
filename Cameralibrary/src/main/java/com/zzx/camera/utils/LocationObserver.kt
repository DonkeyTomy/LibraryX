package com.zzx.camera.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import com.zzx.utils.converter.GpsConverter
import com.zzx.utils.zzx.ZZXMiscUtils

/**@author Tomy
 * Created by Tomy on 2018/11/7.
 */
class LocationObserver(context: Context): LocationListener {
    private val mLocation by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    init {
        init(context)
    }

    @SuppressLint("MissingPermission")
    private fun init(context: Context) {
        AndPermission.with(context)
                .runtime().permission(Permission.ACCESS_COARSE_LOCATION).permission(Permission.ACCESS_FINE_LOCATION)
                .onGranted {
                    mLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5f, this)
                }
                .start()
    }

    fun release() {
        mLocation.removeUpdates(this)
    }


    override fun onLocationChanged(location: Location) {
        location?.apply {
            val latitude = GpsConverter.getGpsString(latitude)
            val longitude = GpsConverter.getGpsString(longitude)
            ZZXMiscUtils.writeGps("Lon: $longitude; Lat: $latitude")

        }
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String) {
        ZZXMiscUtils.writeGps("Lon: 0; Lat: 0")
    }

    override fun onProviderDisabled(provider: String) {
    }




}