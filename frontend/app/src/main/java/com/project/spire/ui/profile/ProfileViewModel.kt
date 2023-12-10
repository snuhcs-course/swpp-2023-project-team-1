package com.project.spire.ui.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.spire.core.auth.AuthRepository
import com.project.spire.core.user.UserRepository
import com.project.spire.models.Post
import com.project.spire.network.RetrofitClient.Companion.postAPI
import com.project.spire.network.user.response.UserError
import com.project.spire.network.user.response.UserSuccess
import com.project.spire.utils.AuthProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _userId = MutableLiveData<String>().apply { value = "" }
    private val _email = MutableLiveData<String>().apply { value = "" }
    private val _username = MutableLiveData<String>().apply { value = "" }
    private val _bio = MutableLiveData<String>().apply { value = "" }
    private val _profileImageUrl = MutableLiveData<String?>()
    private val _followers = MutableLiveData<Int>().apply { value = 0 }
    private val _following = MutableLiveData<Int>().apply { value = 0 }
    private val _posts = MutableLiveData<List<Post>>().apply { value = emptyList() }
    private val _followingState = MutableLiveData<Int>().apply { value = -1 }
    // -1: Not follow, 0: Requested, 1: Accepted
    private val _isMyProfile = MutableLiveData<Boolean>().apply { value = false }
    private val _photoPickerUri = MutableLiveData<Uri?>().apply { value = null }
    private val _editProfileSuccess = MutableLiveData<Boolean>().apply { value = false }
    private val _editProfileErrorMessage = MutableLiveData<String?>().apply { value = null }
    private val _profileLoaded = MutableLiveData<Boolean>().apply { value = false }
    private val _postLoaded = MutableLiveData<Boolean>().apply { value = false }

    val userId: LiveData<String> = _userId
    val email: LiveData<String> = _email
    val username: LiveData<String> = _username
    val bio: LiveData<String> = _bio
    val profileImageUrl: LiveData<String?> = _profileImageUrl
    val followers: LiveData<Int> = _followers
    val following: LiveData<Int> = _following
    val posts: LiveData<List<Post>> = _posts
    val followingState: LiveData<Int> = _followingState
    val isMyProfile: LiveData<Boolean> = _isMyProfile
    val photoPickerUri: LiveData<Uri?> = _photoPickerUri
    val editProfileSuccess: LiveData<Boolean> = _editProfileSuccess
    val editProfileErrorMessage: LiveData<String?> = _editProfileErrorMessage
    val profileLoaded: LiveData<Boolean> = _profileLoaded
    val postLoaded: LiveData<Boolean> = _postLoaded


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

            _userId.postValue(myInfo.id)
            _email.postValue(myInfo.email)
            _username.postValue(myInfo.username)
            _bio.postValue(myInfo.bio)
            _profileImageUrl.postValue(myInfo.profileImageUrl)
            _followers.postValue(followInfo!!.followerCnt)
            _following.postValue(followInfo.followingCnt)
            // _posts.postValue(myInfo?.posts)
            _followingState.postValue(-1)
            _isMyProfile.postValue(true)
            _profileLoaded.postValue(true)
            getMyPosts()
        }

    }

    fun getUserInfo(userId: String) {
        viewModelScope.launch {
            val accessToken = AuthProvider.getAccessToken()
            val userInfo = userRepository.getUserInfo(accessToken, userId)
            val followInfo = userRepository.getFollowInfo(accessToken, userId)

            _userId.postValue(userId)
            _email.postValue(userInfo?.email)
            _username.postValue(userInfo?.username)
            _bio.postValue(userInfo?.bio)
            _profileImageUrl.postValue(userInfo?.profileImageUrl)
            _followers.postValue(followInfo!!.followerCnt)
            _following.postValue(followInfo.followingCnt)
            _followingState.postValue(followInfo.followingStatus)
            // _posts.postValue(userInfo?.posts)
            _isMyProfile.postValue(false)
            _profileLoaded.postValue(true)
            getUserPosts(userId)
        }
    }

    fun updateProfile(username: String, bio: String, profileImage: Uri?, context: Context) {
        viewModelScope.launch {
            val accessToken = AuthProvider.getAccessToken()
            val updateProfile = userRepository.updateMyInfo(accessToken, username, bio, profileImage, context)
            if (updateProfile is UserSuccess) {
                _username.postValue(updateProfile.username)
                _bio.postValue(updateProfile.bio)
                _profileImageUrl.postValue(updateProfile.profileImageUrl)
                _editProfileSuccess.postValue(true)
            } else if (updateProfile is UserError) {
                Log.e("ProfileViewModel", "Update profile error")
                _editProfileErrorMessage.postValue(updateProfile.message)
            } else {
                Log.e("ProfileViewModel", "Update profile error")
                _editProfileErrorMessage.postValue("Unknown error")
            }
        }
    }

    fun followRequest(id: String?) {
        // assume that getUserInfo() called before
        // follow, unfollow, cancel follow
        if (_isMyProfile.value!!) {
            return
        }
        var followUserId: String? = id
        if (followUserId == null) followUserId = _userId.value!!
        viewModelScope.launch {
            val accessToken = AuthProvider.getAccessToken()
            if (_followingState.value!! == -1) {
                // send follow request
                val followRequest = userRepository.follow(accessToken, followUserId)
                if (followRequest) _followingState.postValue(0)
            }
            else if (_followingState.value!! == 0) {
                // cancel follow request
                val cancelFollowRequest = userRepository.cancelFollowRequest(accessToken, followUserId)
                if (cancelFollowRequest) _followingState.postValue(-1)
            }
            else {
                // unfollow
                val unfollowRequest = userRepository.unfollow(accessToken, followUserId)
                if (unfollowRequest) _followingState.postValue(-1)
            }
        }
    }

    private fun getMyPosts() {
        viewModelScope.launch {
            val accessToken = AuthProvider.getAccessToken()
            val response = postAPI.getMyPosts(accessToken, 30, 0)
            // TODO: implement pagination
            if (response.isSuccessful) {
                val successBody = response.body()
                Log.d("ProfileViewModel", "Get my posts response: ${successBody?.total}")
                _posts.postValue(successBody?.items)
                _postLoaded.postValue(true)
            } else {
                val errorBody = response.errorBody()
                Log.e("ProfileViewModel", "Get my posts error: ${errorBody?.string()!!}")
            }
        }
    }

    private fun getUserPosts(userId: String) {
        viewModelScope.launch {
            val accessToken = AuthProvider.getAccessToken()
            val response = postAPI.getUserPosts(accessToken, userId, 30, 0)
            // TODO: implement pagination
            if (response.isSuccessful) {
                val successBody = response.body()
                Log.d("ProfileViewModel", "Get user posts response: ${successBody?.total}")
                _posts.postValue(successBody?.items)
                _postLoaded.postValue(true)
            } else {
                val errorBody = response.errorBody()
                Log.e("ProfileViewModel", "Get user posts error: ${errorBody?.string()!!}")
            }
        }
    }

    fun unregister() {
        viewModelScope.launch {
            val accessToken = AuthProvider.getAccessToken()
            val unregister = authRepository.unregister(accessToken)
            if (unregister) {
                _logoutSuccess.postValue(true)
            }
        }
    }
}

