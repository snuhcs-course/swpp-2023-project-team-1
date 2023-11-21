package com.project.spire.ui.feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.spire.models.Comment
import com.project.spire.models.Post
import com.project.spire.models.User
import com.project.spire.network.RetrofitClient
import com.project.spire.network.post.response.PostSuccess
import com.project.spire.utils.AuthProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val FETCH_SIZE = 10

class FeedViewModel : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>().apply {
        value = emptyList()
    }
    private val _totalPosts = MutableLiveData<Int>().apply {
        value = 0
    }
    private val _nextCursor = MutableLiveData<Int?>().apply {
        value = null
    }
    private val _postLiked = MutableLiveData<Int?>().apply {
        value = null
    }

    val posts: LiveData<List<Post>> = _posts
    val nextCursor: LiveData<Int?> = _nextCursor
    val postLiked: LiveData<Int?> = _postLiked

    fun getInitialPosts() {
        viewModelScope.launch {
            val accessToken = AuthProvider.getAccessToken()
            val response = RetrofitClient.postAPI.getFeedPosts("Bearer $accessToken", FETCH_SIZE, 0)

            if (response.isSuccessful && response.code() == 200) {
                // Feed fetch success
                Log.d(
                    "FeedViewModel",
                    "Fetch feed success with total ${response.body()?.total} and nextCursor ${response.body()?.nextCursor}"
                )
                val body = response.body()
                if (body != null) {
                    _posts.value = body.items
                    _totalPosts.value = body.total
                    _nextCursor.value = body.nextCursor
                }
            } else {
                // Feed fetch failed
                Log.e(
                    "FeedViewModel",
                    "Fetch feed failed with error code ${response.code()} ${response.message()}"
                )
            }
        }
    }

    fun getMorePosts() {
        viewModelScope.launch {
            val accessToken = AuthProvider.getAccessToken()
            val response = RetrofitClient.postAPI.getFeedPosts(
                "Bearer $accessToken",
                FETCH_SIZE,
                _nextCursor.value!!
            )

            if (response.isSuccessful && response.code() == 200) {
                // Feed fetch success
                Log.d(
                    "FeedViewModel",
                    "Fetch feed success with total ${response.body()?.total} and nextCursor ${response.body()?.nextCursor}"
                )
                val body = response.body()
                if (body != null) {
                    _posts.value = _posts.value!! + body.items
                    _totalPosts.value = body.total
                    _nextCursor.value = body.nextCursor
                }
            } else {
                // Feed fetch failed
                Log.e(
                    "FeedViewModel",
                    "Fetch feed failed with error code ${response.code()} ${response.message()}"
                )
            }
        }
    }

    fun likePost(position: Int) {
        viewModelScope.launch {
            val accessToken = AuthProvider.getAccessToken()
            val post = _posts.value!![position]
            val postId = post.postId
            val response = RetrofitClient.postAPI.likePost("Bearer $accessToken", postId)

            if (response.isSuccessful && response.code() == 200) {
                // Like post success
                Log.d("FeedViewModel", "Like post success")
                val isLiked = when (post.isLiked) {
                    1 -> 0
                    else -> 1
                }
                val likeCount = when (post.isLiked) {
                    1 -> post.likeCount - 1
                    else -> post.likeCount + 1
                }
                val newPost = _posts.value!![position].copy(isLiked = isLiked, likeCount = likeCount)
                _posts.value = _posts.value!!.toMutableList().apply {
                    set(position, newPost)
                }
                _postLiked.value = position
            } else {
                // Like post failed
                Log.e(
                    "FeedViewModel",
                    "Like post failed with error code ${response.code()} ${response.message()}"
                )
            }
        }
    }
}