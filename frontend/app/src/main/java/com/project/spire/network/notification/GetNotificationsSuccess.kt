package com.project.spire.network.notification

import com.google.gson.annotations.SerializedName
import com.project.spire.models.Noti
import com.project.spire.models.User

data class GetNotificationsSuccess (
    @SerializedName("total")
    var total: Int,

    @SerializedName("items")
    var notifications: List<Noti>,

    @SerializedName("next_cursor")
    var nextCursor: Int?
    ) {
    // TODO
}
