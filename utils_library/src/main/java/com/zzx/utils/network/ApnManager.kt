package com.zzx.utils.network

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.Telephony

/**@author Tomy
 * Created by Tomy on 2023/10/18.
 */
object ApnManager {

    val APN_URI   = Uri.parse("content://telephony/carriers")
    val CURRENT_APN_URI = Uri.parse("content://telephony/carriers/preferapn")

    fun addAPN(context: Context, apn: String): Int {
        var id = -1
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put("name", apn)
            put("apn", apn)
        }
        return id
    }

}