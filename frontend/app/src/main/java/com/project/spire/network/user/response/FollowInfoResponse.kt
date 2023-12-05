package com.project.spire.network.user.response

import com.google.gson.annotations.SerializedName

interface FollowInfoResponse {
}

data class FollowInfoSuccess (
    @SerializedName("follower_cnt")
    val followerCnt: Int,
    @SerializedName("following_cnt")
    val followingCnt: Int,
    @SerializedName("follower_status")
    val followerStatus: Int,
    @SerializedName("following_status")
    val followingStatus: Int
): FollowInfoResponse

data class FollowInfoError (
    @SerializedName("message")
    val message: String,
): FollowInfoResponse
