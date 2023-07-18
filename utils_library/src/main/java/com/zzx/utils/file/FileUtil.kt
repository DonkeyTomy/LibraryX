package com.zzx.utils.file

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import timber.log.Timber
import java.io.*
import java.math.BigDecimal
import java.util.*


/**@author Tomy
 * Created by Tomy on 2015/7/30.
 */
object FileUtil {

    fun sortDirTime(path: String, dec: Boolean = true, filter: FileFilter? = null): ArrayList<File> {
        return sortDirTime(File(path), dec, filter)
    }

    /**@param dec if true 则递减排序,反之递增(越新的越在上).
     */
    @Synchronized
    fun sortDirTime(dir: File, dec: Boolean = true, filter: FileFilter? = null): ArrayList<File> {
        val list = ArrayList<File>()

        if (!dir.exists() || !dir.isDirectory) {
            return list
        }
        val files = if (filter == null) dir.listFiles() else dir.listFiles(filter)
        if (files == null || files.isEmpty()) {
            return list
        }
        Collections.addAll(list, *files)
        list.sortWith(Comparator { file1, file2 ->
            /*try {
                if (file1.name.toInt() < file2.name.toInt()) {
                    return@Comparator if (dec) -1 else 1
                }
            } catch (e: Exception) {

            }*/
            if (file1.lastModified() < file2.lastModified()) {
                if (dec) -1 else 1
            } else if (file1.lastModified() == file2.lastModified()) {
                0
            } else {
                if (dec) 1 else -1
            }
        })
        return list
    }

    /**@param dec if true 则递减排序,反之递增.
     */
    fun sortDirLongTime(dir: File, dec: Boolean = true, filter: FileFilter? = null): ArrayList<File> {
        val list = ArrayList<File>()

        if (!dir.exists() || !dir.isDirectory) {
            return list
        }
        val files = if (filter == null) dir.listFiles() else dir.listFiles(filter)
        if (files == null || files.isEmpty()) {
            return list
        }
        Collections.addAll(list, *files)
        list.sortWith(Comparator { file1, file2 ->
            try {
                if (file1.name.toLong() < file2.name.toLong()) {
                    if (dec) -1 else 1
                } else if (file1.lastModified() == file2.lastModified()) {
                    0
                } else {
                    if (dec) 1 else -1
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                return@Comparator -1
            }
        })
        return list
    }

    fun checkDirExist(dir: File, needCreate: Boolean = false, needLog: Boolean = false): Boolean {
        val exist = dir.exists() && dir.isDirectory
        if (needLog) {
            Timber.d("dir.path = ${dir.absolutePath}. exist = $exist, needCreate = $needCreate")
        }
        if (!exist) {
            if (needCreate) {
                val success = dir.mkdirs()
                if (!success) {
                    Timber.e("create dir failed!!!")
                }
                return success
            }
        }
        return exist
    }

    fun checkDirExist(dirPath: String, needCreate: Boolean = false): Boolean {
        return checkDirExist(File(dirPath), needCreate)
    }

    fun checkFileExist(filePath: String): Boolean {
        return checkFileExist(File(filePath))
    }

    fun checkFileExist(file: File): Boolean {
        return file.exists() && file.isFile
    }

    fun getDirFileCount(dir: File): Int {
        if (!checkDirExist(dir)) {
            return 0
        }
        return dir.listFiles()?.size ?: 0
    }

    fun getDirFreeSpace(dir: File): Long {
        val stateFs = StatFs(dir.absolutePath)
        return stateFs.availableBlocksLong * stateFs.blockSizeLong
    }

    fun getDirFreeSpace(dir: String): Long {
        return getDirFreeSpace(File(dir))
    }

    fun getDirFreeSpaceByMB(dir: File): Long {
        return getDirFreeSpace(dir) / 1024 / 1024
    }

    fun getFileLengthByMB(bytes: Long): Float {
        return bytes / 1024 / 1024f
    }

    fun getFileLengthByKb(bytes: Long): Float {
        return bytes / 1024f
    }

    fun getDirFreeSpaceByGB(dir: String): Long {
        return getDirFreeSpaceByMB(File(dir)) / 1024
    }

    fun getDirTotalSpaceByGB(dir: File): Long {
        return getDirTotalSpaceByMB(dir) / 1024
    }

    fun getDirTotalSpaceByGB(dir: String): Long {
        return getDirTotalSpaceByMB(File(dir)) / 1024
    }

    fun getDirFreeSpaceByMB(dir: String): Long {
        return getDirFreeSpace(File(dir)) / 1024 / 1024
    }

    fun getDirTotalSpaceByMB(dir: File): Long {
        return getDirTotalSpace(dir) / 1024 / 1024
    }

    fun getDirTotalSpaceByMB(dir: String): Long {
        return getDirTotalSpace(dir) / 1024 / 1024
    }

    fun getDirTotalSpace(dir: File): Long {
        val stateFs = StatFs(dir.absolutePath)
        return stateFs.blockCountLong * stateFs.blockSizeLong
    }

    fun getDirTotalSpace(dir: String): Long {
        return getDirTotalSpace(File(dir))
    }

    fun getFormatSize(size: Double): String {
        val kiloByte = size / 1024
        if (kiloByte < 1) {
            return size.toString() + "Byte(s)"
        }
        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(kiloByte.toString())
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB"
        }
        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(megaByte.toString())
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB"
        }
        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(gigaByte.toString())
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB"
        }
        val result4 = BigDecimal(teraBytes)
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB"
    }

    fun getStorageList(): Array<String> {
        val array = Array(2) {
            ""
        }
        var process: Process? = null
        var inputStream: InputStream? = null
        try {
            process = Runtime.getRuntime().exec("mount")
            inputStream = process.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String? = null
            while (reader.readLine().apply { line = this } != null) {
                if (line!!.contains("sdcardfs") && line!!.contains("storage")) {
                    if (line!!.contains("data")) {
                        val internalStorage = getStoragePath(line!!)
                        array[0] = internalStorage
                    } else if (line!!.contains("mnt")) {
                        val externalStorage = getStoragePath(line!!)
                        array[1] = externalStorage
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
                process?.destroy()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return array
    }

    fun getExternalStoragePath(): String {
        return getStorageList()[1]
    }

    private fun getStoragePath(line: String): String {
        val list = line.split(" ")
        for (element in list) {
            if (element.contains("storage")) {
                return element
            }
        }
        return ""
    }

    private fun getStorageManager(context: Context): StorageManager {
        return context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getVolumeList(context: Context): List<StorageVolume> {
        return getStorageManager(context).storageVolumes
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun checkExternalStorageMounted(context: Context): Boolean {
        return getExternalStorageState(context) == Environment.MEDIA_MOUNTED
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun checkExternalStorageMountable(context: Context): Boolean {
        return getExternalStorageState(context) == Environment.MEDIA_UNMOUNTED
    }

    /**
     * @param context Context
     * @return String
     * @see [Environment.MEDIA_MOUNTED][Environment.MEDIA_BAD_REMOVAL][Environment.MEDIA_UNMOUNTED] [Environment.MEDIA_EJECTING] [Environment.MEDIA_UNMOUNTABLE]
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun getExternalStorageState(context: Context): String {
        val state = getExternalStorageVolume(context)?.state ?: Environment.MEDIA_REMOVED
        Timber.v("state = $state")
        return state
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getExternalStorageVolume(context: Context): StorageVolume? {
        getVolumeList(context).forEach {
            if (it.isRemovable) {
                return it
            }
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("DiscouragedPrivateApi", "PrivateApi",
        "SoonBlockedPrivateApi"
    )
    fun getExternalStoragePath(context: Context): String {
        try {
            val getPathMethod = StorageVolume::class.java.getDeclaredMethod("getPath")
            getExternalStorageVolume(context)?.let {
                return getPathMethod.invoke(it) as String
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
//        return Environment.getExternalStorageDirectory().absolutePath
        return ""
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getStoragePath(context: Context): String {
        var path = getExternalStoragePath(context)
        if (TextUtils.isEmpty(path) || path == "null") {
            path = Environment.getExternalStorageDirectory().absolutePath
        }
        return path
    }

    fun deleteFile(file: File) {
        try {
            val isFile = file.isFile
            val cmd = if (isFile) "rm ${file.absolutePath}" else "rm -rf ${file.absolutePath}"
            Timber.e(cmd)
            val process = Runtime.getRuntime().exec(cmd)
            process.waitFor()
            process.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteFile(file: String) {
        deleteFile(File(file))
    }

    fun openAssignFile(context: Context, file: File) {
        try {
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getItemContentUri(context, file.absolutePath)
            } else {
                Uri.fromFile(file)
            }
            Timber.e("openAssignFile.path = ${uri?.path}")
            val intent = Intent(Intent.ACTION_VIEW).apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setDataAndType(uri, "image/*")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getItemContentUri(context: Context, mAbsolutePath: String): Uri? {
        val projection = arrayOf(MediaColumns._ID)
        val where = MediaColumns.DATA + " = ?"
        val baseUri = MediaStore.Files.getContentUri("external")
        var c: Cursor? = null
        val provider = "com.android.providers.media.MediaProvider"
        var itemUri: Uri? = null
        context.grantUriPermission(provider, baseUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            c = context.contentResolver.query(baseUri,
                    projection,
                    where,
                    arrayOf(mAbsolutePath), null)
            if (c != null && c.moveToNext()) {
                val id = c.getInt(c.getColumnIndexOrThrow(MediaColumns._ID))
                if (id != 0) {
                    itemUri = Uri.withAppendedPath(baseUri, id.toString())
                }
            }
        } catch (e: Exception) {
        } finally {
            c?.close()
        }
        return itemUri
    }

    fun openAssignFolder(context: Context, dir: File, authority: String) {
        if (!checkDirExist(dir)) {
            return
        }
        try {
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                FileProvider.getUriForFile(context, authority, dir)
            } else {
                Uri.fromFile(dir)
            }
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                addCategory(Intent.CATEGORY_DEFAULT)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setDataAndType(uri, "file/*")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    const val PACKAGE_NAME_FILE    = "com.mediatek.filemanager"
    const val CLASS_NAME_FILE      = "com.mediatek.filemanager.FileManagerOperationActivity"
    const val INTENT_EXTRA_SELECT_PATH = "select_path"

    fun openMtkFolder(context: Context, dir: File) {
        if (!checkDirExist(dir, true)) {
            return
        }
        val intent = Intent().apply {
            setClassName(PACKAGE_NAME_FILE, CLASS_NAME_FILE)
            putExtra(INTENT_EXTRA_SELECT_PATH, dir.absolutePath)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
