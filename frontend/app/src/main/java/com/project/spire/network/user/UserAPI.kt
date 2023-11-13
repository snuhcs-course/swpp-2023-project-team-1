package com.project.spire.network.user

import com.project.spire.network.user.request.UserRequest
import com.project.spire.network.user.request.UserUpdate
import com.project.spire.network.user.response.FollowListSuccess
import com.project.spire.network.user.response.UserSuccess
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.Path

interface UserAPI {

    @GET("user/me")
    suspend fun getMyInfo(@Header("Authorization") token: String): Response<UserSuccess>

    @Multipart
    @PATCH("user/me")
    suspend fun updateMyInfo(
        @Header("Authorization") token: String,
        @Part("user_update") request: UserUpdate,
        @Part file: MultipartBody.Part
    ): Response<UserSuccess>

    @Multipart
    @PATCH("user/me")
    suspend fun updateMyInfoWithoutImage(
        @Header("Authorization") token: String,
        @Part("user_update") request: UserUpdate
    ): Response<UserSuccess>

    @GET("user/{user_id}")
    suspend fun getUserInfo(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String
    ): Response<UserSuccess>

    @GET("user/{user_id}/followers")
    suspend fun getFollowers(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String
    ): Response<FollowListSuccess>

    @GET("user/{user_id}/followings")
    suspend fun getFollowings(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String
    ): Response<FollowListSuccess>
}