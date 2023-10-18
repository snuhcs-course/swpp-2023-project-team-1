package com.project.spire.network.auth

import com.google.gson.annotations.SerializedName

interface LoginResponse {
}

data class LoginSuccess (
    @SerializedName("access_token")
    var accessToken: String? = null,

    @SerializedName("refresh_token")
    var refreshToken: String? = null,

    @SerializedName("user_id")
    var userId: String? = null,

    @SerializedName("username")
    var username: String? = null
) : LoginResponse

data class LoginError (
    @SerializedName("message")
    var message: String? = null
) : LoginResponse
