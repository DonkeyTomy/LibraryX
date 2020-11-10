package com.zzx.utils.file

import com.zzx.utils.CommonConst
import timber.log.Timber
import java.io.File

/**@author Tomy
 * Created by Tomy on 2018/6/18.
 */
class FileLocker(private var mLockDir: File?): IFileLocker() {



    override fun isLockFileFull(): Boolean {
        return FileUtil.getDirFileCount(getLockDir()!!) >= LOCK_FILE_COUNT
    }


    override fun lockFile(file: File): Boolean {
        if (!FileUtil.checkFileExist(file)) {
            return false
        }

        try {
            if (!FileUtil.checkDirExist(mLockDir!!, true)) {
                return false
            }
            val runtime = Runtime.getRuntime()
            Timber.e(" ============= startLock ============= ")
            val process = runtime.exec("mv ${file.absolutePath} ${mLockDir!!.absolutePath}")
            process.waitFor()
            process.destroy()
            Timber.e(" ============= finishLock ============= ")
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    fun startLock(file: File): Int {
        try {
            if (!FileUtil.checkDirExist(mLockDir!!, true)) {
                return CommonConst.FAILED
            }
            val runtime = Runtime.getRuntime()
            Timber.e(" ============= startLock ============= ")
            val process = runtime.exec("mv ${file.absolutePath} ${mLockDir!!.absolutePath}")
            process.waitFor()
            process.destroy()
            Timber.e(" ============= finishLock ============= ")
            return CommonConst.SUCCESS
        } catch (e: Exception) {
            e.printStackTrace()
            return CommonConst.FAILED
        }
    }

    override fun setLockDir(dir: File) {
        mLockDir = dir
    }

    override fun deleteLastLockFile() {

    }

    fun getLockDir(): File? {
        return mLockDir
    }

    companion object {
        const val LOCK_FILE_COUNT   = 10
    }
}