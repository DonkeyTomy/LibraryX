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

    val APN_URI   = Uri.parse("content://telephony/carriers")
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
                    put("apn_id", id)
                }
                resolver.update(PREFERRED_APN_URI, preferApn, null, null)
            }
        }
        return id
    }

    fun getSIMInfo(context: Context): String {
        return context.getSystemService<TelephonyManager>()!!.simOperator
    }

}