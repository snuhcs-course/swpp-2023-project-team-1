package com.project.spire.ui.relationship

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.spire.models.RelatedUser
import com.project.spire.models.User
import com.project.spire.network.RetrofitClient
import com.project.spire.network.user.response.FollowItems
import com.project.spire.network.user.response.FollowListSuccess
import com.project.spire.utils.AuthProvider
import kotlinx.coroutines.launch

const val LIMIT = 10

class RelationshipViewModel : ViewModel() {
    private val _users =
        MutableLiveData<List<FollowItems>>().apply { value = emptyList() }
    private var _total = 0
    private var _nextCursor: Int? = 0

    val users: LiveData<List<FollowItems>> = _users

    fun getRelationship(userId: String, type: String) {
        if (_nextCursor != null) {
            when (type) {
                "followers" -> {
                    getFollowers(userId)
                }

                "following" -> {
                    getFollowing(userId)
                }
            }
        }
    }

    private fun getFollowers(userId: String) {
        viewModelScope.launch {
            val accessToken = AuthProvider.getAccessToken()
            val response =
                RetrofitClient.userAPI.getFollowers("Bearer $accessToken", userId, LIMIT, _nextCursor!!)

            if (response.isSuccessful && response.code() == 200) {
                Log.d("RelationshipViewModel", "Get followers response: ${response.body()}")
                val followListSuccess = response.body()!!
                _users.value = followListSuccess.items
                _total = followListSuccess.total
                _nextCursor = followListSuccess.nextCursor
            } else {
                Log.e("RelationshipViewModel", "Error fetching followers: ${response.code()}")
            }
        }
    }

    private fun getFollowing(userId: String) {
        viewModelScope.launch {
            val accessToken = AuthProvider.getAccessToken()
            val response =
                RetrofitClient.userAPI.getFollowings("Bearer $accessToken", userId, LIMIT, _nextCursor!!)

            if (response.isSuccessful && response.code() == 200) {
                Log.d("RelationshipViewModel", "Get following response: ${response.body()}")
                val followListSuccess = response.body()!!
                _users.value = followListSuccess.items
                _total = followListSuccess.total
                _nextCursor = followListSuccess.nextCursor
            } else {
                Log.e("RelationshipViewModel", "Error fetching following: ${response.code()}")
            }
        }
    }
}