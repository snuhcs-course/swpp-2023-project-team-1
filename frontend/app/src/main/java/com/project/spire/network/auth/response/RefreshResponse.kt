package com.project.spire.network.auth.response

import com.google.gson.annotations.SerializedName

interface RefreshResponse {
}

data class RefreshSuccess(
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("refresh_token")
    val refreshToken: String,
) : RefreshResponse

data class RefreshError(
    @SerializedName("message")
    val message: String,
) : RefreshResponse