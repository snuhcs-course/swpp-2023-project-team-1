package com.project.spire.network.post.response

import com.google.gson.annotations.SerializedName

interface UpdatePostResponse {
}

data class UpdatePostSuccess(

    @SerializedName("content")
    val content: String,

    @SerializedName("image_url")
    val imageUrl: String,

    @SerializedName("id")
    val id: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    @SerializedName("user")
    val user: List<String>,

    @SerializedName("like_cnt")
    val likeCnt: Int,

    @SerializedName("comment_cnt")
    val commentCnt: Int,

    @SerializedName("is_liked")
    val isLiked: Int

) : UpdatePostResponse

data class UpdatePostError(

    @SerializedName("message")
    val message: String

) : UpdatePostResponse