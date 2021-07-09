package com.zzx.network.retrofit

import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**@author Tomy
 * Created by Tomy on 9/7/2021.
 */
interface FileApiService {

    @Multipart
    @POST
    fun postFile(@Part partList: List<MultipartBody.Part>): Observable<ResponseBody>

    @FormUrlEncoded
    @Streaming
    @POST
    fun downloadFileWithPost()
}