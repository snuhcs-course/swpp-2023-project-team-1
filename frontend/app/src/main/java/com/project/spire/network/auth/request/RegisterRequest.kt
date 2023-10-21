package com.project.spire.network.auth.request

import com.google.gson.annotations.SerializedName

data class RegisterRequest(

    @SerializedName("email")
    val email: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String,
)