package com.project.spire.models

import com.google.gson.annotations.SerializedName

class Post(

    @SerializedName("id")
    val postId: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("image_url")
    val imageUrl: String,

    @SerializedName("origin_image_url")
    val originalImageUrl: String?,

    @SerializedName("mask_image_url")
    val maskImageUrl: String?,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    @SerializedName("user")
    val user: User,

    @SerializedName("like_cnt")
    val likeCount: Int,

    @SerializedName("comment_cnt")
    val commentCount: Int,

    @SerializedName("is_liked")
    val isLiked: Int
) {
    override fun toString(): String {
        return "Post(postId='$postId', content='$content', imageUrl='$imageUrl', originalImage='$originalImageUrl', createdAt='$createdAt', updatedAt='$updatedAt', user=$user, likeCount=$likeCount, commentCount=$commentCount, isLiked=$isLiked)"
    }

    fun copy(
        postId: String = this.postId,
        content: String = this.content,
        imageUrl: String = this.imageUrl,
        originalImageUrl: String? = this.originalImageUrl,
        maskImageUrl: String? = this.maskImageUrl,
        createdAt: String = this.createdAt,
        updatedAt: String = this.updatedAt,
        user: User = this.user,
        likeCount: Int = this.likeCount,
        commentCount: Int = this.commentCount,
        isLiked: Int = this.isLiked
    ): Post {
        return Post(postId, content, imageUrl, originalImageUrl, maskImageUrl, createdAt, updatedAt, user, likeCount, commentCount, isLiked)
    }
}
