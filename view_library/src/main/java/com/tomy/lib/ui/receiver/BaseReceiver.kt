package com.tomy.lib.ui.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 27/8/2020.
 */
abstract class BaseReceiver(var mContext: Context): BroadcastReceiver() {

    abstract val mActionList: ArrayList<String>

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun registerReceiver() {
        IntentFilter().apply {
            mActionList.forEach {
                addAction(it)
            }
            Timber.d("registerReceiver: ${mActionList[0]}")
            mContext.registerReceiver(this@BaseReceiver, this)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("onReceiver = ${intent.action}")
    }

    fun unregisterReceiver() {
        mContext.unregisterReceiver(this)
    }

}