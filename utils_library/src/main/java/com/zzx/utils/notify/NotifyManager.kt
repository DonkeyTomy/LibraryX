package com.zzx.utils.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.zzx.utils.TTSToast
import timber.log.Timber

class NotifyManager constructor(var context: Context) {

    private val builder by lazy {
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(context.getString(mTitle))
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }


    private val notificationManager by lazy {
        context.getSystemService(NotificationManager::class.java).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID, CHANNEL_ID,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = context.getString(mTitle)
                }
                createNotificationChannel(channel)
            }
        }
    }

    private var mTitle: Int = 0

    fun sendProgressNotify(progressMax: Int, currentProgress: Int, title: Int, finishMsg: Int) {
        mTitle = title
        if (progressMax == currentProgress) {
            val msg = context.getString(finishMsg)
            builder.apply {
                setContentTitle(msg)
                setSilent(false)
//                setContentIntent(PendingIntent.getActivity(context, 0, intent, 0))
                setSmallIcon(android.R.drawable.stat_sys_download_done)
                setAutoCancel(true)
            }
            TTSToast.showComplete(msg)
        }
        builder.setProgress(progressMax, currentProgress, false)
        Timber.d("sendProgressNotify: $progressMax[$currentProgress]")
        notificationManager.notify(100, builder.build())
    }

    fun cancel() {
        notificationManager.cancel(100)
    }

    companion object {
        const val CHANNEL_ID    = "downloadFile"

    }

}