package com.project.spire.ui.feed

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.spire.models.Comment
import com.project.spire.models.Post
import com.project.spire.network.RetrofitClient
import com.project.spire.network.post.request.NewCommentRequest
import com.project.spire.utils.AuthProvider
import kotlinx.coroutines.launch

const val COMMENT_LIMIT = 10

class PostViewModel: ViewModel() {

    private val _post = MutableLiveData<Post>()
    private val _comments = MutableLiveData<List<Comment>>()
    private val _fetchError = MutableLiveData<Boolean>()
    private val _myProfileImage = MutableLiveData<String>()

    val post: MutableLiveData<Post> get() = _post
    val comments: MutableLiveData<List<Comment>> get() = _comments
    val fetchError: MutableLiveData<Boolean> get() = _fetchError
    val myProfileImage: MutableLiveData<String> get() = _myProfileImage

    fun loadPost(postId: String) {
        viewModelScope.launch {
            val accessToken = AuthProvider.getAccessToken()
            val response = RetrofitClient.postAPI.getPost("Bearer $accessToken", postId)

            if (response.code() == 200 && response.isSuccessful && response.body() != null) {
                Log.d("PostViewModel", "${response.body() as Post}")
                _post.value = response.body()
            } else {
                Log.e("PostViewModel", "Error fetching post with ${response.code()} ${response.message()}")
                _fetchError.value = true
            }
        }
    }

    fun loadInitialComments() {
        viewModelScope.launch {
            val accessToken = AuthProvider.getAccessToken()
            val response = RetrofitClient.postAPI.getComments("Bearer $accessToken", _post.value?.postId!!, COMMENT_LIMIT, 0)

            if (response.code() == 200 && response.isSuccessful && response.body() != null) {
                Log.d("PostViewModel", "${response.body()!!.items}")
                _comments.value = response.body()!!.items
            } else {
                Log.e("PostViewModel", "Error fetching comments with ${response.code()} ${response.message()}")
                _fetchError.value = true
            }
        }
    }

    fun comment(content: String) {
        viewModelScope.launch {
            val request = NewCommentRequest(content)
            val accessToken = AuthProvider.getAccessToken()
            val response = RetrofitClient.postAPI.newComment("Bearer $accessToken", _post.value?.postId!!, request)

            if (response.code() == 200 && response.isSuccessful && response.body() != null) {
                Log.d("PostViewModel", "${response.body() as Comment}")
                _comments.value = _comments.value?.plus(response.body()!!)
            } else {
                Log.e("PostViewModel", "Error commenting with ${response.code()} ${response.message()}")
            }
        }
    }

    fun likePost() {
        // TODO
    }

    fun deletePost() {
        // TODO
    }

    fun editPost() {
        // TODO
    }

    fun likeComment() {
        // TODO
    }

    fun deleteComment() {
        // TODO
    }

    fun editComment() {
        // TODO
    }
}