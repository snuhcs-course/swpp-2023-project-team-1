package com.project.spire.network.user

import androidx.compose.ui.geometry.Offset
import com.project.spire.network.user.request.UserRequest
import com.project.spire.network.user.response.FollowInfoSuccess
import com.project.spire.network.user.response.FollowListSuccess
import com.project.spire.network.user.response.UserSuccess
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface UserAPI {

    @GET("user/me")
    suspend fun getMyInfo(@Header("Authorization") token: String): Response<UserSuccess>

    @Multipart
    @PATCH("user/me")
    suspend fun updateMyInfo(
        @Header("Authorization") token: String,
        @Part("user_update") request: UserRequest,
        @Part file: MultipartBody.Part
    ): Response<UserSuccess>

    @Multipart
    @PATCH("user/me")
    suspend fun updateMyInfoWithoutImage(
        @Header("Authorization") token: String,
        @Part("user_update") request: UserRequest
    ): Response<UserSuccess>

    @GET("user/{user_id}")
    suspend fun getUserInfo(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String
    ): Response<UserSuccess>

    @GET("user/{user_id}/followers")
    suspend fun getFollowers(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Response<FollowListSuccess>

    @GET("user/{user_id}/followings")
    suspend fun getFollowings(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Response<FollowListSuccess>

    @POST("user/{user_id}/follow_request")
    suspend fun follow(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String
    ): Response<Void>

    @DELETE("user/{user_id}/cancel_request")
    suspend fun cancelFollowRequest(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String
    ): Response<Void>

    @POST("user/{user_id}/accept_request")
    suspend fun acceptFollowRequest(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String
    ): Response<Void>

    @DELETE("user/{user_id}/reject_request")
    suspend fun rejectFollowRequest(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String
    ): Response<Void>

    @DELETE("user/{user_id}/unfollow")
    suspend fun unfollow(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String
    ): Response<Void>

    @GET("user/{user_id}/follow_info")
    suspend fun followInfo(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String,
    ): Response<FollowInfoSuccess>
}