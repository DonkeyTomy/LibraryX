package com.zzx.camera.h9.addition

import com.zzx.utils.file.IFileLocker
import java.io.File

/**@author Tomy
 * Created by Tomy on 2018/10/27.
 */
class FileLocker: IFileLocker() {

    override fun lockFile(file: File): Boolean {
        return try {
            val newName = "${file.nameWithoutExtension}_IMP${file.extension}"
            file.renameTo(File(file.parent, newName))
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun isLockFileFull(): Boolean {

        return false
    }

    override fun deleteLastLockFile() {
    }

    override fun setLockDir(dir: File) {
    }

}