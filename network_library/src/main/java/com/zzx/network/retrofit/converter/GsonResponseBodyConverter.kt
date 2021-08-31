package com.zzx.network.retrofit.converter

import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonToken
import com.zzx.network.retrofit.ApiDataModel
import com.zzx.network.retrofit.ErrorCode
import com.zzx.network.retrofit.exception.BadRequestException
import com.zzx.network.retrofit.exception.UrlNotFoundException
import com.zzx.network.retrofit.exception.TokenInvalidException
import okhttp3.ResponseBody
import retrofit2.Converter
import timber.log.Timber
import java.io.IOException

/**
 * @author Tomy
 * Created by Tomy on 30/8/2021.
 */
class GsonResponseBodyConverter<T>(
    private val gson: Gson, private val adapter: TypeAdapter<T>
) : Converter<ResponseBody, T> {

    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T? {
        val apiModel = adapter.fromJson(value.charStream()) as ApiDataModel<T>
        Timber.d("apiModel = $apiModel")
        return when (apiModel.errorCode) {
            ErrorCode.TOKEN_INVALID -> throw TokenInvalidException()
            ErrorCode.NOT_FOUND -> throw UrlNotFoundException()
            ErrorCode.BAD_REQUEST   -> throw BadRequestException()
            else -> {
                if (apiModel.success) {
                    apiModel.data
                } else {
                    null
                }
            }
        }
        /*val jsonReader = gson.newJsonReader(value.charStream())
        val result = adapter.read(jsonReader)
        if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
            throw JsonIOException("JSON document was not fully consumed.")
        }
        return result*/
    }
}