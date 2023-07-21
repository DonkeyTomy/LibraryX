package com.zzx.utils.log

import android.annotation.SuppressLint
import android.content.Context
import android.os.Process
import com.zzx.utils.date.TimeFormat
import com.zzx.utils.file.FileUtil
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber
import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

/**@author Tomy
 * Created by Tomy on 2023/7/17.
 */
class LogcatHelper private constructor(private var mContext: Context? = null) {

    private var mLogDir: File? = null

    private var mRunning = false

    init {
        mContext?.let {
            initDir(it)
        }
    }

    fun initDir(context: Context) {
        mLogDir = File(File(FileUtil.getStoragePath(context)), "LogCat")
        FileUtil.checkDirExist(mLogDir!!, true, needLog = true)
    }

    /**
     * @param onlyMyPid Boolean true则只捕获当前进程日志,false则捕获所有日志
     * @return Boolean false代表当前正在捕获日志
     */
    fun startLogCat(onlyMyPid: Boolean = true): Boolean {
        if (!mRunning) {
            mRunning = true
        } else {
            return false
        }
        val pid = Process.myPid()
//        "logcat -v time -f "+logCatFile.getAbsolutePath()+ " libloc:S RPC:S"
        Timber.d("myPid ======= $pid")
        thread {
            var reader: BufferedReader? = null
            var process: java.lang.Process? = null
            var fileWriter: FileWriter? = null
            try {

                fileWriter = FileWriter(File(mLogDir!!, "${
                    TimeFormat.formatFileNameFullTime(
                        System.currentTimeMillis()
                    )
                }.log"), true)
                process = Runtime.getRuntime().exec("logcat")
                reader = BufferedReader(InputStreamReader(process!!.inputStream), 1024)
                var line: String? = null
                while (mRunning && reader.readLine()?.apply { line = this } != null) {
                    if (!mRunning) {
                        break
                    }
                    if (line!!.isEmpty() || (onlyMyPid && !line!!.contains("$pid"))) {
                        continue
                    }
                    fileWriter.write("$line\n ")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                process?.destroy()
                try {
                    reader?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    fileWriter?.flush()
                    fileWriter?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return true
    }

    fun stopLogCat() {
        mRunning = false
    }

    fun catCurrentLog() {
        if (mRunning) {
            return
        }
        Observable.just(Unit)
            .doOnSubscribe {
                startLogCat(false)
            }
            .delay(3000, TimeUnit.MILLISECONDS)
            .subscribe({
                stopLogCat()
            }, {
                it.printStackTrace()
            })
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: LogcatHelper? = null

        fun getInstance(context: Context? = null): LogcatHelper {
            if (INSTANCE == null) {
                INSTANCE = LogcatHelper(context)
            }
            return INSTANCE!!
        }
    }

}