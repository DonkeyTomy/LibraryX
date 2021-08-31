package com.zzx.network.retrofit

import com.google.gson.annotations.SerializedName

/**@author Tomy
 * Created by Tomy on 30/8/2021.
 */
data class ApiDataModel<T> (
    val success: Boolean,
    @SerializedName("errorcode")
    val errorCode: Int,
    val data: T
)
