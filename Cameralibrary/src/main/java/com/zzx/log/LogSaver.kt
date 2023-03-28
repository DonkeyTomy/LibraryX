package com.zzx.log

import android.content.Context
import com.zzx.camera.R
import com.zzx.camera.data.HCameraSettings
import com.zzx.camera.values.CommonConst
import com.zzx.utils.MediaScanUtils
import com.zzx.utils.file.FileUtil
import com.zzx.utils.rxjava.singleThread
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

/**@author Tomy
 * Created by Tomy on 2019/1/1.
 */
object LogSaver {

    private val mTimeSimpleDateFormat by lazy {
        SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.getDefault())
    }

    private val mDateSimpleDateFormat by lazy {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }

    fun writeCapture(context: Context) {
        writeMsgId(context, R.string.perform_capture)
    }

    fun writeRecord(context: Context, on: Boolean = true) {
        writeMsgId(context, if (on) R.string.perform_record else R.string.perform_stop_record)
    }

    fun writeAudioRecord(context: Context, on: Boolean = true) {
        writeMsgId(context, if (on) R.string.perform_record_audio else R.string.perform_stop_record_audio)
    }

    fun writePower(context: Context, on: Boolean = true) {
        writeMsgId(context, if (on) R.string.perform_device_boot else R.string.perform_device_shutdown)
    }

    fun writeMsgId(context: Context, msgId: Int) {
        writeLog(context, context.getString(msgId))
    }


    fun writeLogin(context: Context, account: String) {
        writeLog(context, context.getString(R.string.perform_login, account))
    }

    fun writeLog(context: Context, msg: String) = singleThread {
        if (HCameraSettings(context).isUseExternalStorage() && !FileUtil.checkExternalStorageMounted(context)) {
            return@singleThread
        }
        val logFile = File(getLogPath(context))
        val fileExist = logFile.exists()
        try {
            BufferedWriter(FileWriter(logFile, true)).apply {
                write("${mTimeSimpleDateFormat.format(Date())}: $msg\n")
                flush()
                close()
            }
            if (!fileExist) {
                MediaScanUtils(context.applicationContext, logFile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getLogPath(context: Context): String {
        return File(CommonConst.getLogDir(context), "${mDateSimpleDateFormat.format(Date())}.log").absolutePath
    }

}