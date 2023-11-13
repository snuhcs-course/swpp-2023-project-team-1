package com.project.spire.network.post.request

import com.google.gson.annotations.SerializedName

data class NewCommentRequest (
    @SerializedName("content")
    val content: String
)