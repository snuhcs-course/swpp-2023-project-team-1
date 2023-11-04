package com.project.spire.network.inference

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: temporary client for ML inference
// Should be combined with RetrofitClient later
class InferenceClient {
    companion object {
        private const val LOCAL_URL = "http://10.0.2.2:8080/v2/"

        private val okHttpClient = OkHttpClient.Builder()
            .build()

        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(LOCAL_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val inferenceAPI = retrofit.create(InferenceAPI::class.java)
    }

}