package com.project.spire.models

import com.google.gson.annotations.SerializedName

class Comment (
    @SerializedName("post_id")
    val postId: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("id")
    val id: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    @SerializedName("user")
    val user: User,

    @SerializedName("like_cnt")
    val likeCnt: Int,

    @SerializedName("is_liked")
    val isLiked: Int
) {
    override fun toString(): String {
        return "\"" + content + "\" by @" + user.userName
    }
}