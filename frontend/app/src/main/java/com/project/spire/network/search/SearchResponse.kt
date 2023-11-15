package com.project.spire.network.search

import com.google.gson.annotations.SerializedName
import com.project.spire.models.SearchUser

interface SearchResponse {
}

data class SearchResponseSuccess(

    @SerializedName("total")
    val total: Int,
    @SerializedName("items")
    val items: List<SearchUser>,
    @SerializedName("next_cursor")
    val nextCursor: Int
) : SearchResponse


data class SearchResponseError(

    @SerializedName("message")
    val message: String

) : SearchResponse
