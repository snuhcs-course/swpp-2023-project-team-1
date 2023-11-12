package com.project.spire.ui.feed

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.spire.models.Post
import com.project.spire.network.RetrofitClient
import com.project.spire.utils.AuthProvider
import kotlinx.coroutines.launch

class PostViewModel: ViewModel() {

    private val _post = MutableLiveData<Post>()
    private val _fetchError = MutableLiveData<Boolean>()

    val post: MutableLiveData<Post> get() = _post
    val fetchError: MutableLiveData<Boolean> get() = _fetchError

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

    fun comment() {
        // TODO
    }

    fun like() {
        // TODO
    }

    fun delete() {
        // TODO
    }

    fun edit() {
        // TODO
    }
}