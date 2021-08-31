package com.zzx.network.retrofit.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zzx.network.retrofit.ApiDataModel
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import timber.log.Timber
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @author Tomy
 * Created by Tomy on 30/8/2021.
 */
class GsonConverterFactory private constructor(private val gson: Gson) :
    Converter.Factory() {
    override fun responseBodyConverter(
        type: Type, annotations: Array<Annotation>, retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        val newType = object : ParameterizedType {
            override fun getActualTypeArguments(): Array<Type> {
                return arrayOf(type)
            }

            override fun getRawType(): Type {
                return ApiDataModel::class.java
            }

            override fun getOwnerType(): Type? {
                return null
            }

        }
        val adapter = gson.getAdapter(TypeToken.get(newType))
        Timber.v("getResponseAdapter = $adapter")
        return GsonResponseBodyConverter(gson, adapter)
    }

    override fun requestBodyConverter(
        type: Type, parameterAnnotations: Array<Annotation>, methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody> {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return GsonRequestBodyConverter(gson, adapter)
    }

    companion object {
        /**
         * Create an instance using `gson` for conversion. Encoding to JSON and decoding from JSON
         * (when no charset is specified by a header) will use UTF-8.
         */ // Guarding public API nullability.
        /**
         * Create an instance using a default [Gson] instance for conversion. Encoding to JSON and
         * decoding from JSON (when no charset is specified by a header) will use UTF-8.
         */
        @JvmOverloads
        fun create(gson: Gson? = Gson()): GsonConverterFactory {
            if (gson == null) throw NullPointerException("gson == null")
            return GsonConverterFactory(gson)
        }
    }
}