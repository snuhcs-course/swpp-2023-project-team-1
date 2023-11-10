package com.project.spire.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.spire.models.Comment
import com.project.spire.models.Post
import com.project.spire.models.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FeedViewModel : ViewModel() {
    private val postMutableList = mutableListOf<Post>()
    private val _posts = MutableLiveData<List<Post>>().apply {
        value = emptyList()
    }
    val posts: LiveData<List<Post>> = _posts

    fun fetchPosts() {
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
                val postId = 123
                val content = "This is a post"
                val imageUrl = "https://i.pravatar.cc/1000"
                val likedUsers = ArrayList<User>()
                val comments = ArrayList<Comment>()
                val createdAt = "1 hours ago"
                val updatedAt = "1 hours ago"

                val post = Post(
                    userId, user, content, imageUrl,
                    likedUsers, comments, createdAt, updatedAt
                )

                postMutableList.add(post)
                _posts.value = postMutableList
            }
        }
    }
}