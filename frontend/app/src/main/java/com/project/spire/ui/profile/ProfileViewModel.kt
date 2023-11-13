package com.project.spire.ui.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.spire.core.auth.AuthRepository
import com.project.spire.core.user.UserRepository
import com.project.spire.models.Post
import com.project.spire.utils.AuthProvider
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
    private val _isFollowRequested = MutableLiveData<Boolean>().apply { value = false }
    private val _isMyProfile = MutableLiveData<Boolean>().apply { value = false }
    private val _photoPickerUri = MutableLiveData<Uri?>().apply { value = null }

    val email: LiveData<String> = _email
    val username: LiveData<String> = _username
    val bio: LiveData<String> = _bio
    val profileImageUrl: LiveData<String> = _profileImageUrl
    val followers: LiveData<Int> = _followers
    val following: LiveData<Int> = _following
    val posts: LiveData<List<Post>> = _posts
    val isFollowed: LiveData<Boolean> = _isFollowed
    val isFollowRequested: LiveData<Boolean> = _isFollowRequested
    val isMyProfile: LiveData<Boolean> = _isMyProfile
    val photoPickerUri: LiveData<Uri?> = _photoPickerUri


    private val _logoutSuccess = MutableLiveData<Boolean>()
    val logoutSuccess: LiveData<Boolean> = _logoutSuccess

    fun setPhotoPickerUri(uri: Uri?) {
        _photoPickerUri.postValue(uri)
    }

    fun logout() {
        viewModelScope.launch {
            val accessToken = AuthProvider.getAccessToken()
            val logout = authRepository.logout(accessToken)
            _logoutSuccess.postValue(logout)
        }
    }

    fun getMyInfo() {
        viewModelScope.launch {
            val accessToken = AuthProvider.getAccessToken()
            val myInfo = userRepository.getMyInfo(accessToken)
            val followInfo = userRepository.getFollowInfo(accessToken, myInfo?.id!!)

            _email.postValue(myInfo?.email)
            _username.postValue(myInfo?.username)
            _bio.postValue(myInfo?.bio)
            _profileImageUrl.postValue(myInfo?.profileImageUrl)
            _followers.postValue(followInfo!!.followerCnt)
            _following.postValue(followInfo!!.followingCnt)
            // _posts.postValue(myInfo?.posts)
            _isFollowed.postValue(false)
            _isFollowRequested.postValue(false)
            _isMyProfile.postValue(true)
        }
    }

    fun getUserInfo(userId: String) {
        viewModelScope.launch {
            val accessToken = AuthProvider.getAccessToken()
            val userInfo = userRepository.getUserInfo(accessToken, userId)
            val followInfo = userRepository.getFollowInfo(accessToken, userId)

            _email.postValue(userInfo?.email)
            _username.postValue(userInfo?.username)
            _bio.postValue(userInfo?.bio)
            _profileImageUrl.postValue(userInfo?.profileImageUrl)
            _followers.postValue(followInfo!!.followerCnt)
            _following.postValue(followInfo!!.followingCnt)

            if (followInfo.followerStatus == -1) {
                _isFollowed.postValue(false)
                _isFollowRequested.postValue(false)
            }
            else if (followInfo.followerStatus == 0) {
                _isFollowed.postValue(false)
                _isFollowRequested.postValue(true)
            }
            else if (followInfo.followerStatus == 1) {
                _isFollowed.postValue(true)
                _isFollowRequested.postValue(false)
            }
            // _posts.postValue(userInfo?.posts)
            _isMyProfile.postValue(false)
        }
    }

    fun updateProfile(username: String, bio: String, profileImage: Uri?, context: Context) {
        viewModelScope.launch {
            val accessToken = AuthProvider.getAccessToken()
            val updateProfile = userRepository.updateMyInfo(accessToken, username, bio, profileImage, context)

            _username.postValue(updateProfile?.username)
            _bio.postValue(updateProfile?.bio)
            _profileImageUrl.postValue(updateProfile?.profileImageUrl)
        }
    }

    fun followRequest(userId: String) {
        // assume that getUserInfo() called before
        // follow, unfollow, cancel follow
        if (_isMyProfile.value!!) {
            return
        }
        viewModelScope.launch {
            val accessToken = AuthProvider.getAccessToken()
            if (!_isFollowed.value!!) {
                if (!_isFollowRequested.value!!) {
                    // send follow request
                    val followRequest = userRepository.follow(accessToken, userId)
                    if (followRequest) {
                        _isFollowed.postValue(false) // TODO?
                        _isFollowRequested.postValue(true)
                    }
                }
                else {
                    // cancel follow request
                    val cancelFollowRequest = userRepository.cancelFollowRequest(accessToken, userId)
                    if (cancelFollowRequest) {
                        _isFollowed.postValue(false)
                        _isFollowRequested.postValue(false)
                    }
                }
            }
            else {
                // unfollow
                val unfollowRequest = userRepository.unfollow(accessToken, userId)
                if (unfollowRequest) {
                    _isFollowed.postValue(false)
                    _isFollowRequested.postValue(false)
                }
            }
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