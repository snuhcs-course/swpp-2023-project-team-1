package com.project.spire.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.spire.core.auth.AuthRepository
import com.project.spire.core.user.UserRepository
import com.project.spire.models.Post
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _email = MutableLiveData<String>().apply { value = "" }
    private val _username = MutableLiveData<String>().apply { value = "" }
    private val _bio = MutableLiveData<String>().apply { value = "" }
    private val _profileImageUrl = MutableLiveData<String>().apply { value = "" }
    private val _followers = MutableLiveData<Int>().apply { value = 0 }
    private val _following = MutableLiveData<Int>().apply { value = 0 }
    private val _posts = MutableLiveData<List<Post>>().apply { value = emptyList() }
    private val _isFollowed = MutableLiveData<Boolean>().apply { value = false }
    private val _isMyProfile = MutableLiveData<Boolean>().apply { value = false }

    val email: LiveData<String> = _email
    val username: LiveData<String> = _username
    val bio: LiveData<String> = _bio
    val profileImageUrl: LiveData<String> = _profileImageUrl
    val followers: LiveData<Int> = _followers
    val following: LiveData<Int> = _following
    val posts: LiveData<List<Post>> = _posts
    val isFollowed: LiveData<Boolean> = _isFollowed
    val isMyProfile: LiveData<Boolean> = _isMyProfile


    private val _logoutSuccess = MutableLiveData<Boolean>()
    val logoutSuccess: LiveData<Boolean> = _logoutSuccess

    fun logout() {
        viewModelScope.launch {
            val accessToken = authRepository.accessTokenFlow.first()
            val logout = authRepository.logout(accessToken)
            _logoutSuccess.postValue(logout)
        }
    }

    fun getMyInfo() {
        viewModelScope.launch {
            val accessToken = authRepository.accessTokenFlow.first()
            val myInfo = userRepository.getMyInfo(accessToken)
            _email.postValue(myInfo?.email)
            _username.postValue(myInfo?.username)
            _bio.postValue(myInfo?.bio)
            _profileImageUrl.postValue(myInfo?.profileImageUrl)
            // _followers.postValue(myInfo?.followers)
            // _following.postValue(myInfo?.following)
            // _posts.postValue(myInfo?.posts)
            _isFollowed.postValue(false)
            _isMyProfile.postValue(true)
        }
    }

    fun getUserInfo(userId: String) {
        viewModelScope.launch {
            val accessToken = authRepository.accessTokenFlow.first()
            val userInfo = userRepository.getUserInfo(accessToken, userId)
            _email.postValue(userInfo?.email)
            _username.postValue(userInfo?.username)
            _bio.postValue(userInfo?.bio)
            _profileImageUrl.postValue(userInfo?.profileImageUrl)
            // _followers.postValue(userInfo?.followers)
            // _following.postValue(userInfo?.following)
            // _posts.postValue(userInfo?.posts)
            // _isFollowed.postValue(false)
            _isMyProfile.postValue(false)
        }
    }
}

class ProfileViewModelFactory(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(authRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}