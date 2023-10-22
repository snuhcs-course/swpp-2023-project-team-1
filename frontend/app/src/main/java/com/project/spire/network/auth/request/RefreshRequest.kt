package com.project.spire.network.auth.request

import com.google.gson.annotations.SerializedName

data class RefreshRequest(

    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("refresh_token")
    val refreshToken: String,
)