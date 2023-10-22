package com.project.spire.network.auth.response

import com.google.gson.annotations.SerializedName

interface RegisterResponse {
}

data class RegisterSuccess(

    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("refresh_token")
    val refreshToken: String,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("username")
    val username: String

) : RegisterResponse

data class RegisterError(

    @SerializedName("message")
    val message: String

) : RegisterResponse