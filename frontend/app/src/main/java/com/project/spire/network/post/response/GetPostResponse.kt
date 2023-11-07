package com.project.spire.network.post.response

import com.google.gson.annotations.SerializedName

interface GetPostResponse {
}

data class GetPostSuccess(

    @SerializedName("content")
    val content: String,

    @SerializedName("post_image_url")
    val postImageUrl: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String

) : GetPostResponse

data class GetPostError(

    @SerializedName("message")
    val message: String

) : GetPostResponse