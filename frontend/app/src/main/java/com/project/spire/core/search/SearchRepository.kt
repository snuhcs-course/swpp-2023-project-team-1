package com.project.spire.core.search

import com.project.spire.network.RetrofitClient
import com.project.spire.network.search.SearchRequest
import com.project.spire.network.search.SearchSuccess

class SearchRepository {
    suspend fun searchUser(request: SearchRequest): SearchSuccess? {
        val response = RetrofitClient.searchAPI.searchUser()
        
    }
}