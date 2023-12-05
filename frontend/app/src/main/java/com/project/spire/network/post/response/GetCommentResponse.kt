package com.project.spire.network.post.response

import com.google.gson.annotations.SerializedName
import com.project.spire.models.Comment

interface GetCommentResponse {
}

data class GetCommentSuccess(

    @SerializedName("total")
    val total: Int,

    @SerializedName("items")
    val items: List<Comment>,

    @SerializedName("next_cursor")
    val nextCursor: String,

    ) : GetCommentResponse

data class GetCommentError(

    @SerializedName("message")
    val message: String

) : GetCommentResponse