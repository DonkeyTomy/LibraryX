package com.zzx.camera

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import com.bumptech.glide.Glide
import com.zzx.camera.service.CameraService
import com.zzx.utils.ExceptionHandler
import com.zzx.utils.TTSToast
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2018/9/11.
 */
class MyApplication: Application() {

    @SuppressLint("WorldWriteableFiles")
    override fun onCreate() {
        super.onCreate()
        if (Timber.treeCount() <= 0) {
            Timber.plant(Timber.DebugTree())
        }
        TTSToast.init(this)
//        PreferenceSaver.init(this, mode = Context.MODE_WORLD_WRITEABLE)
        ExceptionHandler.getInstance(this,"Camera")
//        CrashReport.testJavaCrash()
//        startRecord(false)
    }

    private fun startRecord(record: Boolean) {
        val intent = Intent(this, CameraService::class.java)
        intent.action = if (record) CameraService.START_RECORD else CameraService.START_PREVIEW
        startService(intent)
    }

    override fun onTerminate() {
        super.onTerminate()
        TTSToast.release()
        ExceptionHandler.getInstance(this,"Camera").release()
        Timber.e("onTerminate()")
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Timber.e("onTrimMemory. level = $level")
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(this).clearMemory()
        }
        Glide.get(this).trimMemory(level)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Timber.e("onLowMemory.")
        Glide.get(this).clearMemory()
    }

}