package com.project.spire.network.user.request

import com.google.gson.annotations.SerializedName

data class UserRequest (
    @SerializedName("username")
    val username: String,
    @SerializedName("bio")
    val bio: String,
    @SerializedName("profile_image_url")
    val profile_image_url: String
)
