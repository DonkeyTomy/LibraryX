package com.zzx.utils.network

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.Telephony.Carriers
import android.telephony.TelephonyManager
import androidx.core.content.getSystemService
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2023/10/18.
 */
object ApnManager {

    val PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn")

    fun setAPN(context: Context, name: String, apn: String): Int {
        var id = -1
        val NUMBERIC = getSIMInfo(context)
        Timber.v("numberic: $NUMBERIC")
        Timber.v("name: $name")
        Timber.v("apn: $apn")
        if (NUMBERIC.isEmpty()) {
            return id
        }
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(Carriers.NAME, name)
            put(Carriers.APN, apn)
            put(Carriers.TYPE, "default")
            put(Carriers.NUMERIC, NUMBERIC)
            put(Carriers.MCC, NUMBERIC.substring(0, 3))
            put(Carriers.MNC, NUMBERIC.substring(3, NUMBERIC.length))
        }
        resolver.insert(Carriers.CONTENT_URI, values)?.apply {
            val cursor = resolver.query(this, null, null, null, null)
            cursor?.let {
                val idIndex = it.getColumnIndex("_id")
                it.moveToFirst()
                id = it.getInt(idIndex)
                it.close()
                Timber.d("id: $id")
                val preferApn = ContentValues().apply {
                    put(PREFER_APN_ID, id)
                }
                resolver.update(PREFERRED_APN_URI, preferApn, null, null)
            }
        }
        return id
    }

    fun getPreferApn(context: Context): ArrayList<String> {
        val apn = ArrayList<String>()
        context.contentResolver.query(PREFERRED_APN_URI, arrayOf(Carriers.APN, Carriers.NAME), null, null, null)?.let {
            if (it.moveToFirst()) {
                it.getColumnIndex(Carriers.NAME).apply {
                    if (this >= 0) {
                        apn.add(it.getString(this))
                    }
                }
                it.getColumnIndex(Carriers.APN).apply {
                    if (this >= 0) {
                        apn.add(it.getString(this))
                    }
                }
                it.close()
            }
        }
        return apn
    }

    fun getPreferApnId(context: Context): Int {
        var preferId = -1
        context.contentResolver.query(PREFERRED_APN_URI, null, null, null, null)?.let {
            if (it.moveToFirst()) {
                Timber.d("has data")
                it.columnNames.forEach { name ->
                    Timber.v("column: $name")
                }
                val idIndex = it.getColumnIndex(PREFER_APN_ID)
                if (idIndex >= 0) {
                    preferId = it.getInt(idIndex)
                } else {
                    Timber.e("No prefer apn id column")
                }
            } else {
                Timber.w("No prefer apn")
            }
            it.close()
        }
        return preferId
    }



    fun getSIMInfo(context: Context): String {
        return context.getSystemService<TelephonyManager>()!!.simOperator
    }

    const val PREFER_APN_ID = "apn_id"

}