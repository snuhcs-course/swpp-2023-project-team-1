package com.project.spire.network.inference

import com.project.spire.network.RetrofitClient
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

// TODO: temporary client for ML inference
// Should be combined with RetrofitClient later
class InferenceClient {
    companion object {
        private const val BASE_URL = "http://localhost:8080/v2/"

        private val okHttpClient = OkHttpClient.Builder()
            .build()

        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val inferenceAPI = retrofit.create(InferenceAPI::class.java)
    }

}