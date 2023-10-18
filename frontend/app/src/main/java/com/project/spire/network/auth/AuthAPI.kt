package com.project.spire.network.auth

import com.project.spire.network.auth.request.LoginRequest
import com.project.spire.network.auth.response.LoginResponse
import com.project.spire.network.auth.response.LoginSuccess
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthAPI {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    // TODO: other API endpoints here
}
