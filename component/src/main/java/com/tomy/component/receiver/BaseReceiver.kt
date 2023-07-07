package com.tomy.component.receiver

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

    private var mCallback: OnReceiver? = null

    private var mRegistered = false

    fun setReceiveCallback(callback: OnReceiver?) {
        mCallback = callback
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun registerReceiver() {
        Timber.d("registerReceiver start")
        if (mRegistered) {
            return
        }
        mRegistered = true
        IntentFilter().apply {
            mActionList.forEach {
                Timber.d("registerReceiver: $it")
                addAction(it)
            }
            mContext.registerReceiver(this@BaseReceiver, this)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("onReceiver = ${intent.action}")
        try {
            mCallback?.onReceiver(context, intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun unregisterReceiver() {
        if (mRegistered) {
            mRegistered = false
            mContext.unregisterReceiver(this)
        }
    }

    interface OnReceiver {
        fun onReceiver(context: Context, intent: Intent)
    }

}