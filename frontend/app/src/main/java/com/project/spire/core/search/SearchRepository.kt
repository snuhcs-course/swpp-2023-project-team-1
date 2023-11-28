package com.project.spire.core.search

import android.util.Log
import com.project.spire.network.RetrofitClient
import com.project.spire.network.search.SearchSuccess

const val FETCH_SIZE = 10

class SearchRepository {
    suspend fun searchUsers(accessToken: String, searchString: String, offset: Int): SearchSuccess? {
        val response = RetrofitClient.searchAPI.searchUser("Bearer $accessToken", searchString, FETCH_SIZE, offset)
        return if (response.isSuccessful && response.code() == 200) {
            Log.d("SearchRepository","Search user success: ${response.body()?.total}, offset $offset and nextCursor ${response.body()?.nextCursor}")
            val body = response.body()
            body
        } else {
            val errorBody = response.errorBody()
            Log.e("SearchRepository","Search user failed: ${errorBody?.string()!!}")
            null
        }
    }
}