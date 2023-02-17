package com.zzx.camera.utils

import android.content.Context
import android.provider.MediaStore
import com.zzx.utils.rxjava.fixedThread
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**@author Tomy
 * Created by Tomy on 2018/6/14.
 */
class MediaInfoUtils {

    companion object {
        private var mPrePictureTime: String = ""

        private var mCount = 0

        private val mTimeFormatter by lazy {
            SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
        }

        private val mDateFormat by lazy {
            SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        }

        fun getDateDir(): String {
            return mDateFormat.format(Date())
        }


        fun getFileName(suffix: String, prefix: String = ""): String {
            return "$prefix${mTimeFormatter.format(Date())}$suffix"
        }

        fun getFileNameWithoutSuffix(): String {
            return mTimeFormatter.format(Date())
        }

        fun getPictureName(): String {
            var pictureTime = getFileNameWithoutSuffix()
            if (pictureTime == mPrePictureTime) {
                mCount++
                mPrePictureTime = pictureTime
                pictureTime += "_$mCount"
            } else {
                mCount = 0
                mPrePictureTime = pictureTime
            }
            return "$pictureTime$PICTURE"
        }

        fun getVideoName(): String {
            return getFileName(VIDEO_MP4, TYPE_VIDEO_PRE)
        }

        fun getTmpFileName(): String {
            return getFileName(FILE_TMP)
        }

        fun getAudioName(): String {
            return getFileName(AUDIO_MP3)
        }

        fun tmpFile2Video(filePath: String) {
            tmpFile2Video(File(filePath))
        }

        fun tmpFile2Video(file: File?) = fixedThread {
            file?.renameTo(File(file.parent, "${file.nameWithoutExtension}$VIDEO_MP4"))
        }

        fun tmpFileName2VideoName(file: File): File {
            return File(file.parent, "${file.nameWithoutExtension}$VIDEO_MP4")
        }


        fun tmpFile2ImpVideo(file: File?) {
            file?.renameTo(File(file.parent, "${file.nameWithoutExtension}$FILE_IMP$VIDEO_MP4"))
        }

        fun tmpFileName2VideoImpName(file: File): File {
            return File(file.parent, "${file.nameWithoutExtension}$FILE_IMP$VIDEO_MP4")
        }

        fun insertImage(context: Context, file: File) = fixedThread {
            MediaStore.Images.Media.insertImage(context.contentResolver, file.absolutePath, file.name, null)
        }

        const val TIME_FORMAT = "yyyyMMdd-HHmmss"
        const val DATE_FORMAT = "yyyyMMdd"

        const val FILE_TMP  = ".tmp"
        const val VIDEO_MP4 = ".mp4"
        const val AUDIO_MP3 = ".mp3"
        const val AUDIO_AAC = ".aac"
        const val PICTURE   = ".png"
        const val FILE_IMP  = "_IMP"

        const val TYPE_VIDEO_PRE    = "0_"
        const val TYPE_PIC_PRE      = "1_"
        const val TYPE_AUDIO_PRE    = "2_"
    }

}