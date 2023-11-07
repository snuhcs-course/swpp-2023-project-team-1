package com.project.spire.network

import com.project.spire.network.auth.AuthAPI
import com.project.spire.network.user.UserAPI
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    companion object {
        private const val BASE_URL = "http://10.0.2.2:8000/api/"

        private var tokenInterceptor = TokenInterceptor()

        private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor)
            .build()

        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val authAPI: AuthAPI = retrofit.create(AuthAPI::class.java)
        val userAPI: UserAPI = retrofit.create(UserAPI::class.java)
    }
}
