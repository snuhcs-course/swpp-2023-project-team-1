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

    private val postMutableList = mutableListOf<Post>()
    private val _posts = MutableLiveData<List<Post>>().apply {
        value = emptyList()
    }
    private val _totalPosts = MutableLiveData<Int>().apply {
        value = 0
    }
    private val _nextCursor = MutableLiveData<Int>().apply {
        value = 0
    }

    val posts: LiveData<List<Post>> = _posts

    fun getInitialPosts() {
        viewModelScope.launch {
            val accessToken = AuthProvider.getAccessToken()

            // FIXME: Should be changed to getFeedPosts
            val response = RetrofitClient.postAPI.getFeedPosts("Bearer $accessToken", FETCH_SIZE, 0)

            if (response.isSuccessful) {
                // Feed fetch success
                Log.d("FeedViewModel", "Fetch feed success")
                val body = response.body()
                if (body != null) {
                    val posts = body.items.sortedBy { it.updatedAt }  // Latest post first
                    _posts.value = posts
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
        // TODO implement this

    }

    fun fetchDummyPosts() {
        // TODO implement this
        // fetch posts from backend
        viewModelScope.launch {
            // FIXME: Remove this dummy delay
            delay(2000)

            // FIXME: Remove this dummy data
            for (i in 1..10) {
                val userId = "345"
                val username = "donghaahn"
                val profileImage = "https://i.pravatar.cc/1000"
                val user = User(userId, username, profileImage)
                val postId = "123"
                val content = "This is a post"
                val imageUrl = "https://i.pravatar.cc/1000"
                val likedUsers = ArrayList<User>()
                val comments = ArrayList<Comment>()
                val createdAt = "1 hours ago"
                val updatedAt = "1 hours ago"

                _posts.value = postMutableList
            }
        }
    }
}