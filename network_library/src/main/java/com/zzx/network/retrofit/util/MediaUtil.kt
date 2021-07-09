package com.zzx.network.retrofit.util

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.net.URLConnection

/**@author Tomy
 * Created by Tomy on 9/7/2021.
 */
object MediaUtil {

    fun guessMimeType(path: String): MediaType? {
        val fileNameMap = URLConnection.getFileNameMap()
        val temp = path.replace("#", "")
        var contentType = fileNameMap.getContentTypeFor(temp)
        if (contentType == null) {
            contentType = "application/octet-stream"
        }
        return contentType.toMediaTypeOrNull()
    }

}