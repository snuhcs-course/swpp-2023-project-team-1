package com.project.spire.network.search

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SearchAPI {
    @GET("search/user/{search_string}")
    suspend fun searchUser(
        @Header("Authorization") token: String,
        @Path("search_string") searchString: String,
        @Query("limit") limit: Int,
        @Query("Offset") offset: Int
    ): Response<SearchSuccess>

}