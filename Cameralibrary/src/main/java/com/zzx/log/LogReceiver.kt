package com.zzx.log

import android.content.Context
import android.content.Intent
import com.tomy.lib.ui.receiver.BaseReceiver
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2018/12/28.
 */
class LogReceiver(context: Context): BaseReceiver(context) {

    override val mActionList = arrayListOf(
        ACTION_CAPTURE,
        ACTION_RECORD_AUDIO,
        ACTION_RECORD_VIDEO,
        ACTION_LOGIN
    )

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_CAPTURE      -> {
                LogSaver.writeCapture(context)
            }
            ACTION_RECORD_AUDIO -> {
                LogSaver.writeAudioRecord(context, intent.getBooleanExtra(EXTRA_STATE, true))
            }
            ACTION_RECORD_VIDEO -> {
                LogSaver.writeRecord(context, intent.getBooleanExtra(EXTRA_STATE, true))
            }
            ACTION_LOGIN        -> {
                val account = intent.getStringExtra(LOGIN_ACCOUNT)
                LogSaver.writeLogin(context, account ?: "null")
            }
            Intent.ACTION_SHUTDOWN  -> {
                LogSaver.writePower(context, false)
            }
            Intent.ACTION_BOOT_COMPLETED    -> {
                LogSaver.writePower(context)
            }
        }
    }

    companion object {
        const val ACTION_CAPTURE        = "log_capture"
        const val ACTION_RECORD_VIDEO   = "log_video"
        const val ACTION_RECORD_AUDIO   = "log_audio"

        const val EXTRA_STATE   = "extraState"


        const val ACTION_LOGIN  = "log_login"

        const val LOGIN_ACCOUNT = "login_account"
        const val ACCOUNT_ADMIN = "SuperAdmin"
    }
}