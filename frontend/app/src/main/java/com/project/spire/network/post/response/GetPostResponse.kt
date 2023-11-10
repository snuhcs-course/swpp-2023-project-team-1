package com.project.spire.network.post.response

import com.google.gson.annotations.SerializedName
import com.project.spire.models.Post

interface GetPostsResponse {
}

data class GetPostsSuccess(

    @SerializedName("total")
    val total: Int,

    @SerializedName("items")
    val items: List<Post>,

    @SerializedName("next_cursor")
    val nextCursor: Int

) : GetPostsResponse

data class GetPostsError(

    @SerializedName("message")
    val message: String

) : GetPostsResponse