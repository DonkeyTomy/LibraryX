package com.zzx.camera.values

import android.content.Context
import android.os.Environment
import com.zzx.camera.data.HCameraSettings
import com.zzx.media.utils.FileNameUtils
import com.zzx.utils.file.FileUtil
import java.io.File

/**@author Tomy
 * Created by Tomy on 2018/6/20.
 */
object CommonConst {

    private const val FILE_DIR_NAME = "H9"
    private const val DIR_PICTURE   = "pic"
    private const val DIR_VIDEO     = "video"
    private const val DIR_LOCK      = "lock"
    private const val DIR_LOG      = "text"
    private const val DIR_AUDIO     = "audio"

    const val WEATHER_REC  = "com.zzx.txzsdktest_receive_weather"//接听广播
    const val SEND_WEATHER = "com.zzx.txzsdktest_send_weather"//发送广播
    const val SEND_CITY_NAME = "cityName"//当前城市
    const val SEND_CITY_QUALITY = "cityQuality"//当前空气质量
    const val SEND_CITY_WEATHER = "cityWeather"//当前天气
    const val SEND_CITY_TEMPERATURE = "cityTemperature"//当前温度

    const val SEND_FM = "com.zzx.fm"
    const val FM_STATUS = "status"

    const val BLUETOOTH_CONNECTED = "zzxBtConnected"
    const val FM_ENABLED = "zzx_fm_enable"

    /*val FILE_ROOT_DIR   = lazy {
        File("${FileUtil.getExternalStoragePath()}/Android/data/zzx.com.launcher", FILE_DIR_NAME).apply {
            FileUtil.checkDirExist(this, true)
        }
    }*/

    var FILE_ROOT_DIR: File? = null

    private var FILE_VIDEO_DIR: File?   = null
    private var FILE_LOCK_DIR: File?    = null
    private var FILE_PIC_DIR: File?     = null
    private var FILE_LOG_DIR: File?     = null
    private var FILE_AUDIO_DIR: File?   = null

    fun getVideoDir(context: Context, needCreate: Boolean = false): File? {
        checkExternalMounted(context)
        if (FILE_ROOT_DIR != null) {
            FILE_VIDEO_DIR = File(File(FILE_ROOT_DIR, DIR_VIDEO), FileNameUtils.getDateDir())
        }
        if (needCreate) {
            FILE_VIDEO_DIR?.apply {
                FileUtil.checkDirExist(this, true)
            }
        }
        return FILE_VIDEO_DIR
    }

    fun getLogDir(context: Context): File? {
        checkExternalMounted(context)
        if (FILE_ROOT_DIR != null) {
            FILE_LOG_DIR = File(FILE_ROOT_DIR, DIR_LOG)
            FileUtil.checkDirExist(FILE_LOG_DIR!!, true)
        }
        return FILE_LOG_DIR
    }

    private fun getLockDir(context: Context) {
        checkExternalMounted(context)
        if (FILE_ROOT_DIR != null) {
            FILE_LOCK_DIR = File(FILE_ROOT_DIR, DIR_LOCK)
        }
    }

    fun getLockDir(context: Context, fileName: String): File {
        getLockDir(context)
        FILE_LOCK_DIR = File(FILE_LOCK_DIR, fileName.substring(0, 8))
        FILE_LOCK_DIR?.apply {
            FileUtil.checkDirExist(this, true)
        }
        return FILE_LOCK_DIR!!
    }


    fun getPicDir(context: Context): File? {
        checkExternalMounted(context)
        if (FILE_ROOT_DIR != null) {
            FILE_PIC_DIR = File(File(FILE_ROOT_DIR, DIR_PICTURE), FileNameUtils.getDateDir())
        }
        FILE_PIC_DIR?.apply {
            FileUtil.checkDirExist(this, true)
        }
        return FILE_PIC_DIR
    }

    fun getRootDir(context: Context): File? {
        checkExternalMounted(context)
        return FILE_ROOT_DIR
    }

    fun getAudioDir(context: Context): File? {
        checkExternalMounted(context)
        if (FILE_ROOT_DIR != null) {
            FILE_AUDIO_DIR = File(File(FILE_ROOT_DIR, DIR_AUDIO), FileNameUtils.getDateDir())
        }
        return FILE_AUDIO_DIR
    }


    private fun checkExternalMounted(context: Context) {
        FILE_ROOT_DIR = if (HCameraSettings(context).isUseExternalStorage() && FileUtil.checkExternalStorageMounted(context)) {
            File("${FileUtil.getExternalStoragePath(context)}/DCIM")
        } else {
            File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)}")
        }.apply {
            FileUtil.checkDirExist(this, true)
        }
    }


    const val TAG_RECORD_FLOW   = "[RecordFlow] "

    const val FILE_PATH = "FilePath"
    const val FILE_TYPE = "FIleType"
    const val INTENT_FROM   = "intentFrom"
    const val FROM_CAMERA   = 0
    enum class FileType {
        DIR,
        IMAGE,
        VIDEO,
        AUDIO,
        TEXT,
        NON
    }
}