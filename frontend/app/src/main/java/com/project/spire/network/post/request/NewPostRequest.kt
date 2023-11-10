package com.project.spire.network.post.request

import com.google.gson.annotations.SerializedName
import java.util.Base64

data class NewPost (
    @SerializedName("content")
    val content: String,

    @SerializedName("image_url")
    val imageUrl: String = ""
)

data class NewImage (
    @SerializedName("modified_image")
    val modifiedImage: String,

    @SerializedName("original_image")
    val originalImage: String?,

    @SerializedName("mask_image")
    val maskImage: String?,

    @SerializedName("prompt")
    val prompt: String
)

data class NewPostRequest (

    @SerializedName("post")
    val post: NewPost,

    @SerializedName("image")
    val image: NewImage

)