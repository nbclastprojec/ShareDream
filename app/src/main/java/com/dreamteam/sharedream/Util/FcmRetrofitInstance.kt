package com.dreamteam.sharedream.Util

import com.dreamteam.sharedream.Util.Constants.Constants.FCM_KEY
import com.dreamteam.sharedream.Util.Constants.Constants.FCM_URL
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import java.io.IOException

object FcmRetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(FCM_URL)
            .client(provideOkHttpClient(AppInterceptor()))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val fcmApi: FCMInterface by lazy {
        retrofit.create(FCMInterface::class.java)
    }

    //Client
    private fun provideOkHttpClient(
        interceptor: AppInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .run {
            addInterceptor(interceptor)
            build()
        }

    class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain)
                : Response = with(chain) {
            val newRequest = request().newBuilder()
                .addHeader("Authorization", "key=$FCM_KEY")
                .addHeader("Content-Type", "application/json")
                .build()
            proceed(newRequest)
        }
    }
}