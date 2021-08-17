package com.zzx.network.retrofit

import com.google.gson.GsonBuilder
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**@author Tomy
 * Created by Tomy on 14/11/2020.
 */
class RetrofitManager private constructor() {

    inline fun <reified T> init(baseUrl: String, needRxJava: Boolean = true, interceptor: Interceptor? = null, needLog: Boolean = true): T {
        val builder = OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .connectTimeout(5, TimeUnit.SECONDS) //设置网络连接超时时间
            .readTimeout(5, TimeUnit.SECONDS) //设置数据读取超时时间
            .writeTimeout(5, TimeUnit.SECONDS) //设置数据写入超时时间
        if (needLog) {
            builder.addInterceptor(HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Timber.v("HttpMsg: $message")
                }

            }).apply { level = HttpLoggingInterceptor.Level.BODY })
        }

        val pinner = builder.certificatePinner(
            CertificatePinner.Builder()
                .add("sbbic.com", "sha1/C8xoaOSEzPC6BgGmxAt/EAcsajw=")
                .add("closedevice.com", "sha1/T5x9IXmcrQ7YuQxXnxoCmeeQ84c=").build())
        interceptor?.apply {
            builder.addInterceptor(this)
        }
        val client = builder.build()
        val gson = GsonBuilder().setLenient().create()
        val build = Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)
            //将服务器返回的json数据转换为实体类对象
            .addConverterFactory(GsonConverterFactory.create(gson))
        if (needRxJava) {
            //使用Rxjava的Observable方式必须添加此项  使用Retrofit默认Call形式则不需要
            build.addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        }
        return build.build().create(T::class.java)
    }

    companion object {
        private var mRetrofit: RetrofitManager? = null

        fun getInstance(): RetrofitManager {
            if (mRetrofit == null) {
                mRetrofit = RetrofitManager()
            }
            return mRetrofit!!
        }
    }

}