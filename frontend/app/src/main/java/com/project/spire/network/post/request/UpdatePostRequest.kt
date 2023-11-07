package com.project.spire.network.post.request

import com.google.gson.annotations.SerializedName

data class UpdatePostRequest (

    @SerializedName("content")
    val content: String,

    @SerializedName("image_url")
    val imageUrl: String,
)