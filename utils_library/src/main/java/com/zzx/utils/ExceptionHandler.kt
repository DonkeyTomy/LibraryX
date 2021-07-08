package com.zzx.utils

import android.app.Application
import com.tencent.bugly.crashreport.CrashReport
import com.zzx.utils.file.FileUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*

/**@author Tomy
 * Created by Tomy on 2018/8/12.
 */
class ExceptionHandler private constructor(application: Application?, dir: String, id: String): Thread.UncaughtExceptionHandler {
    private val mFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
    private var mContext: Application? = application

    private val LOG_DIR by lazy {
        "${FileUtil.getStoragePath(mContext!!)}/Log/$dir"
    }

    init {
        application?.apply {
            val strategy = CrashReport.UserStrategy(this)
            strategy.apply {
                appPackageName = packageName
                appVersion = packageManager?.getPackageInfo(packageName, 0)?.versionName
                appChannel = appPackageName
                appReportDelay = 5000
//                setCrashHandleCallback(CrashCallback())
            }
            CrashReport.initCrashReport(application, id, true, strategy)
        }

//        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    inner class CrashCallback: CrashReport.CrashHandleCallback() {
        override fun onCrashHandleStart(crashType: Int, errorType: String?, errorMessage: String?, errorStack: String): MutableMap<String, String>? {
            Timber.i("stack = $errorStack")
            saveLog2File(errorStack)
            return null
        }

    }

    fun release() {
        CrashReport.closeBugly()
        mContext = null
//        Thread.setDefaultUncaughtExceptionHandler(null)
    }

    private fun handleException(ex: Throwable) {
        saveException2File(ex)
        ex.printStackTrace()
    }

    @Synchronized
    fun saveException2File(ex: Throwable) {
        if (mContext == null) {
            return
        }
        Observable.just(ex)
                .observeOn(Schedulers.io())
                .subscribe {
                    val stringWriter = StringWriter()
                    val writer = PrintWriter(stringWriter)
                    ex.printStackTrace(writer)
                    var cause = ex.cause
                    while (cause != null) {
                        cause.printStackTrace(writer)
                        cause = cause.cause
                    }
                    writer.close()
                    val result = stringWriter.toString()
                    val time = mFormatter.format(Date())
                    val fileName = "$time-ex.txt"
                    if (LOG_DIR.isNotEmpty()) {
                        val dir = File(LOG_DIR)
                        if (!dir.exists() || !dir.isDirectory) {
                            dir.mkdirs()
                        }
                        try {
                            val fos = FileWriter(File(LOG_DIR, fileName))
                            fos.append("$result \n")
                            fos.append("${ex.message}\n\n")
                            fos.close()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
    }

    @Synchronized
    fun saveLog2File(log: String) {
        if (mContext == null) {
            return
        }
        Observable.just(log)
                .observeOn(Schedulers.io())
                .subscribe {
                    val time = mFormatter.format(Date())
                    val fileName = "$time-save.txt"
                    if (LOG_DIR.isNotEmpty()) {
                        val dir = File(LOG_DIR)
                        if (!dir.exists() || !dir.isDirectory) {
                            dir.mkdirs()
                        }
                        try {
                            File(LOG_DIR, fileName).appendText("$log \n")
                            /*val fos = FileWriter(File(LOG_DIR, fileName))
                            fos.write("$log \n")
                            fos.close()*/
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
            handleException(e)
            mDefaultHandler.uncaughtException(t, e)
    }

    companion object {
        private var mInstance: ExceptionHandler? = null
        fun getInstance(application: Application? = null, dir: String = "", id: String = DEFAULT_ID): ExceptionHandler {
            if (mInstance != null) {
                return mInstance!!
            }
            if (mInstance == null) {
                mInstance = ExceptionHandler(application, dir, id)
            }
            return mInstance!!
        }

        const val TAG = "ExceptionHandler"

        const val DEFAULT_ID = "9ae676a08f"
    }

}