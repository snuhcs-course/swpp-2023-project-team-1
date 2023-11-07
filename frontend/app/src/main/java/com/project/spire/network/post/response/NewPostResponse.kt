package com.project.spire.network.post.response

import com.google.gson.annotations.SerializedName

interface NewPostResponse {
}

data class NewPostSuccess(

    @SerializedName("post_id")
    val postId: String

) : NewPostResponse

data class NewPostError(

    @SerializedName("message")
    val message: String

) : NewPostResponse