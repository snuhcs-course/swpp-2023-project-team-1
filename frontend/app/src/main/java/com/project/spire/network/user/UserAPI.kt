package com.project.spire.network.user

import com.project.spire.network.user.request.UserRequest
import com.project.spire.network.user.response.UserSuccess
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path

interface UserAPI {

    @GET("user/me")
    suspend fun getMyInfo(@Header("Authorization") token: String): Response<UserSuccess>

    @PATCH("user/me")
    suspend fun updateMyInfo(
        @Header("Authorization") token: String,
        @Body request: UserRequest
    ): Response<UserSuccess>

    @GET("user/{user_id}")
    suspend fun getUserInfo(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String
    ): Response<UserSuccess>
}