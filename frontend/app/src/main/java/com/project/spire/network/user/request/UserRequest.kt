package com.project.spire.network.user.request

import com.google.gson.annotations.SerializedName

data class UserRequest (
    @SerializedName("profile_image_url")
    val profileImageUrl: String,
)