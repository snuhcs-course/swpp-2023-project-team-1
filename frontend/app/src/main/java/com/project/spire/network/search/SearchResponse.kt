package com.project.spire.network.search

import com.google.gson.annotations.SerializedName
import com.project.spire.models.UserListItem

interface SearchResponse {
}

data class SearchSuccess(

    @SerializedName("total")
    val total: Int,
    @SerializedName("items")
    val items: List<UserListItem>,
    @SerializedName("next_cursor")
    val nextCursor: Int?

) : SearchResponse


data class SearchError(

    @SerializedName("message")
    val message: String

) : SearchResponse
