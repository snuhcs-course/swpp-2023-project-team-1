package com.project.spire.network.auth

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthAPI {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginSuccess>

    // TODO: other API endpoints here
}
