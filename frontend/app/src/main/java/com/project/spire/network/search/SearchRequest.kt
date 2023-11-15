package com.project.spire.network.search

import com.google.gson.annotations.SerializedName

data class SearchRequest(
    @SerializedName("search_string")
    val searchString: String,
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("offset")
    val offset: Int
)
