package com.project.spire.network.user.response

import com.google.gson.annotations.SerializedName
import com.project.spire.models.RelatedUser
import com.project.spire.models.User

interface FollowListResponse {
}

data class FollowListSuccess (
    @SerializedName("total")
    val total: Int,
    @SerializedName("items")
    val items: List<FollowItems>,
    @SerializedName("next_cursor")
    val nextCursor: Int?
): FollowListResponse

data class FollowListError (
    @SerializedName("message")
    val message: String,
): FollowListResponse

data class FollowItems (
    @SerializedName("id")
    val id: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("profile_image_url")
    val profileImageUrl: String?,

    @SerializedName("is_following")
    val isFollowing: Boolean,

    @SerializedName("is_follower")
    val isFollower: Boolean,
)
