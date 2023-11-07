package com.project.spire.network.user.response

import com.google.gson.annotations.SerializedName

interface UserResponse {
}

data class UserSuccess (
    @SerializedName("email")
    val email: String,

    @SerializedName("id")
    val id: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("profile_image_url")
    val profileImageUrl: String,
): UserResponse

data class UserError (
    @SerializedName("message")
    val message: String,
): UserResponse
