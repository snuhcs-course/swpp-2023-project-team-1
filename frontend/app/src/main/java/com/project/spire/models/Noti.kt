package com.project.spire.models

import com.google.gson.annotations.SerializedName


data class Noti (
    @SerializedName("id")
    var id: String,

    @SerializedName("notification_type")
    var type: String,

    @SerializedName("read_at")
    var readAt: String?,

    @SerializedName("sender_id")
    var senderId: String,

    @SerializedName("sender")
    var sender: User,

    @SerializedName("recipient_id")
    var recipientId: String,

    @SerializedName("recipient")
    var recipient: User,

    @SerializedName("post_id")
    var postId: String?,

    @SerializedName("post_image_url")
    var postImageUrl: String?,

    @SerializedName("created_at")
    var createdAt: String,

    @SerializedName("updated_at")
    var updatedAt: String
) {
    // TODO
}
