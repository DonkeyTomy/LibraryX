package com.tomy.lib.ui.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter

/**@author Tomy
 * Created by Tomy on 27/8/2020.
 */
abstract class BaseReceiver(private var mContext: Context): BroadcastReceiver() {

    open val mActionList = arrayListOf<String>()

    fun getRegisterActionList() {

    }

    fun registerReceiver() {
        IntentFilter().apply {
            mActionList.forEach {
                addAction(it)
            }
            mContext.registerReceiver(this@BaseReceiver, this)
        }
    }

    fun unregisterReceiver() {
        mContext.unregisterReceiver(this)
    }

}