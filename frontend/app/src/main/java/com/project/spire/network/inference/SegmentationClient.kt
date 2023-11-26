package com.project.spire.network.inference

import com.project.spire.network.NetworkConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class SegmentationClient {
    companion object {
        private const val LOCAL_URL = "http://10.0.2.2:8080/v2/"

        private val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .build()

        private val retrofit: Retrofit = Retrofit.Builder()
            //.baseUrl(LOCAL_URL)
            .baseUrl(NetworkConfig.SEGMENTATION_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val segmentationAPI = retrofit.create(SegmentationAPI::class.java)
    }

}