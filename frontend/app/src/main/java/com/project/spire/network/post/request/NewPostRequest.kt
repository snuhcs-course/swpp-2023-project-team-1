package com.project.spire.network.post.request

import com.google.gson.annotations.SerializedName

data class NewPostRequest (

    @SerializedName("post")
    val post: List<String>,

    @SerializedName("image")
    val image: List<String>,
)