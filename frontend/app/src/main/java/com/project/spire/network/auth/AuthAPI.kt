package com.project.spire.network.auth

import com.project.spire.network.auth.request.LoginRequest
import com.project.spire.network.auth.response.LoginSuccess
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST


interface AuthAPI {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginSuccess>

    @GET("auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Void>

    // TODO: other API endpoints here
}