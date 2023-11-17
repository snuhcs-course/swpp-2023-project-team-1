package com.project.spire.models

import com.google.gson.annotations.SerializedName

class SearchUser(
    @SerializedName("id")
    val userId: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("profile_image_url")
    val profileImageUrl: String,
    @SerializedName("is_following")
    val isFollowing: Boolean,
    @SerializedName("is_follower")
    val isFollower: Boolean,
)
