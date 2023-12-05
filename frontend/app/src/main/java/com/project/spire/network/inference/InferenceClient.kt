package com.project.spire.network.inference

import com.project.spire.network.NetworkConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class InferenceClient {
    companion object {
        private const val LOCAL_URL = "http://10.0.2.2:8080/v2/"

        private val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30,TimeUnit.SECONDS)
            .build()

        private val retrofit: Retrofit = Retrofit.Builder()
            //.baseUrl(LOCAL_URL)
            .baseUrl(NetworkConfig.INFERENCE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val inferenceAPI = retrofit.create(InferenceAPI::class.java)
    }

}