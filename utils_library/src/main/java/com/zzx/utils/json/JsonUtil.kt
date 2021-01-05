package com.zzx.utils.json

import com.google.gson.Gson

/**@author Tomy
 * Created by Tomy on 5/1/2021.
 */
object JsonUtil {
}

inline fun <reified T> Gson.convertToStr(jsonStr: String): T {
    return this.fromJson(jsonStr, T::class.java)
}