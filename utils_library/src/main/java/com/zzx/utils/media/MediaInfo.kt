package com.zzx.utils.media

import java.io.File

/**@author Tomy
 * Created by Tomy on 29/5/2021.
 */
object MediaInfo {
    enum class FileSuffix(var value: String) {
        DIR(""),
        PNG("png"),
        JPG("jpg"),
        BMP("bmp"),
        MP4("mp4"),
        AVI("avi"),
        MKV("mkv"),
        MP3("mp3"),
        WAV("wav"),
        AAC("aac"),
        TEXT("txt"),
        TMP("tmp"),
        INI("ini"),
        LOG("log");
    }

    fun isAudio(file: File): Boolean {
        return when (file.extension) {
            FileSuffix.MP3.value, FileSuffix.WAV.value, FileSuffix.AAC.value -> true
            else -> false
        }
    }

    fun isVideo(file: File): Boolean {
        return when (file.extension) {
            FileSuffix.MP4.value, FileSuffix.MKV.value, FileSuffix.AVI.value -> true
            else -> false
        }
    }

    fun isImage(file: File): Boolean {
        return when (file.extension) {
            FileSuffix.PNG.value, FileSuffix.JPG.value, FileSuffix.BMP.value -> true
            else -> false
        }
    }

    fun checkFileType(file: File): String {
        return when (file.extension) {
            FileSuffix.MP4.value, FileSuffix.MKV.value, FileSuffix.AVI.value -> FILE_TYPE_VIDEO
            FileSuffix.MP3.value, FileSuffix.WAV.value, FileSuffix.AAC.value -> FILE_TYPE_AUDIO
            FileSuffix.PNG.value, FileSuffix.JPG.value, FileSuffix.BMP.value -> FILE_TYPE_IMAGE
            else -> ""

        }
    }

    const val FILE_TYPE_VIDEO   = "video"
    const val FILE_TYPE_IMAGE   = "image"
    const val FILE_TYPE_AUDIO   = "audio"
}