package com.project.spire.network.notification

import com.google.gson.annotations.SerializedName
import com.project.spire.models.User

data class GetNotificationsSuccess (
    @SerializedName("total")
    var total: Int,

    @SerializedName("notifications")
    var notifications: List<Notification>,

    @SerializedName("next_cursor")
    var nextCursor: String
    ) {
    // TODO
}

data class Notification (
    @SerializedName("id")
    var id: String,

    @SerializedName("type")
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

    @SerializedName("created_at")
    var createdAt: String,

    @SerializedName("updated_at")
    var updatedAt: String
    ) {
    // TODO
}