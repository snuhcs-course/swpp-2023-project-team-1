package com.project.spire.network.user.response

import com.google.gson.annotations.SerializedName

interface FollowListResponse {
}

data class FollowListSuccess (
    @SerializedName("total")
    val total: Int,
    @SerializedName("items")
    val items: List<FollowItems>,
    @SerializedName("next_cursor")
    val nextCursor: Int
): FollowListResponse

data class FollowListError (
    @SerializedName("message")
    val message: String,
): FollowListResponse

data class FollowItems (
    @SerializedName("email")
    val email: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("bio")
    val bio: String,
    @SerializedName("profile_image_url")
    val profileImageUrl: String
)
