package com.project.spire.network.notification

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NotificationAPI {
    @GET("notification/me")
    suspend fun getMyNotifications(
        @Header("Authorization") accessToken: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Response<GetNotificationsSuccess>
}