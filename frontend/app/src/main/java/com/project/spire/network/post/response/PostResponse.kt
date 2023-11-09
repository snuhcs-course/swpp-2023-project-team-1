package com.project.spire.network.post.response

import com.google.gson.annotations.SerializedName
import com.project.spire.models.User

interface PostResponse {
}

data class PostSuccess(

    @SerializedName("id")
    val postId: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("image_url")
    val imageUrl: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    @SerializedName("user")
    val user: User,

    @SerializedName("like_cnt")
    val likeCount: Int,

    @SerializedName("comment_cnt")
    val commentCount: Int,

    @SerializedName("is_liked")
    val isLiked: Int

) : PostResponse