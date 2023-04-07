package com.zzx.utils.power

import android.content.Context
import android.content.Intent

/**@author Tomy
 * Created by Tomy on 2023/4/7.
 */
object PowerManagerUtil {

    fun shutdown(context: Context, needShowWindow: Boolean) {
        try {
            Intent(ACTION_REQUEST_SHUTDOWN).apply {
                putExtra(EXTRA_KEY_CONFIRM, needShowWindow)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun reboot(context: Context) {
        try {
            Intent(Intent.ACTION_REBOOT).apply {
                putExtra("nowait", 1)
                putExtra("interval", 1)
                putExtra("window", 0)
                context.sendBroadcast(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    const val ACTION_REQUEST_SHUTDOWN = "com.android.internal.intent.action.REQUEST_SHUTDOWN"

    const val EXTRA_KEY_CONFIRM = "android.intent.extra.KEY_CONFIRM"

}