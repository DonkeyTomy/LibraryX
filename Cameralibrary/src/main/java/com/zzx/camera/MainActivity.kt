package com.zzx.camera

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.zzx.camera.adapter.PhotoSettingAdapter
import com.zzx.camera.service.CameraService

/**@author Tomy
 * Created by Tomy on 2018/10/5.
 */
class MainActivity: AppCompatActivity() {
    /*@field:FloatWinContainer
    @Inject
    lateinit var mContainer: View

    @Inject
    lateinit var mCameraPresenter: HCameraPresenter<SurfaceHolder, Camera>*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        startRecord(false)
//        showNotification()
//        showPreferenceDialog()
        /*val manager = TPreferenceManager(this)
        manager.apply {
            inflatePreferenceScreen(R.xml.preference_capture_set)
            bindPreferences(list_item)
        }*/
        findViewById<View>(R.id.startCamera).setOnClickListener {
            startRecord(false)
            /*mCameraPresenter.setPictureCallback(object : ICameraManager.PictureDataCallback {
                override fun onCaptureFinished(buffer: ByteArray) {
                    val outputStream = FileOutputStream("/sdcard/test.png")
                    outputStream.write(buffer)
                    outputStream.close()
                }

            })
            mCameraPresenter.takePicture()*/
        }
        finish()
    }

    @SuppressLint("NotificationPermission")
    private fun showNotification() {
        val channel = NotificationChannel(CameraService::class.java.name, CameraService::class.java.name, NotificationManager.IMPORTANCE_HIGH)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
        val notification = Notification.Builder(this, CameraService::class.java.name)
                .setVisibility(Notification.VISIBILITY_PRIVATE)
                .setSmallIcon(R.drawable.topbar_list_icon)
                .setContentText("Test")
                .setWhen(System.currentTimeMillis())
                .build()
        notification.flags = notification.flags.or(Notification.FLAG_ONGOING_EVENT)
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun startRecord(record: Boolean) {
        val intent = Intent(this, CameraService::class.java)
        intent.action = if (record) CameraService.START_RECORD else CameraService.DISMISS_WINDOW
//        startService(intent)
        startForegroundService(intent)
    }

    fun showPreferenceDialog() {
        val settingAdapter = PhotoSettingAdapter(this)
        settingAdapter.addData(arrayListOf("1", "2", "3", "4", "5"))
        val list = arrayOf("1", "2", "3", "4", "5")
        val builder = AlertDialog.Builder(this, com.tomy.lib.ui.R.style.CustomDialogTheme).apply {
            setSingleChoiceItems(settingAdapter, 1, null)
        }
        builder.show()
    }
}