package com.project.spire.network.post.response

import com.google.gson.annotations.SerializedName

interface GetCommentResponse {
}

data class GetCommentSuccess(

    @SerializedName("content")
    val content: String,

    @SerializedName("post_image_url")
    val postImageUrl: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String

) : GetCommentResponse

data class GetCommentError(

    @SerializedName("message")
    val message: String

) : GetCommentResponse