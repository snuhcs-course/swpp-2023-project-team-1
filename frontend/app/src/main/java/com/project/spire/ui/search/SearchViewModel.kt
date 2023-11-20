package com.project.spire.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.spire.core.auth.AuthRepository
import com.project.spire.core.search.SearchRepository
import com.project.spire.core.user.UserRepository
import com.project.spire.models.SearchUser
import com.project.spire.ui.profile.ProfileViewModel
import com.project.spire.utils.AuthProvider
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchRepository: SearchRepository
) : ViewModel() {


    private val _users = MutableLiveData<List<SearchUser>>().apply {
        value = emptyList()
    }
    private val _totalUsers = MutableLiveData<Int>().apply {
        value = 0
    }
    private val _nextCursor = MutableLiveData<Int?>().apply {
        value = null
    }
    private val _searchString = MutableLiveData<String?>().apply {
        value = null
    }


    val users: LiveData<List<SearchUser>> = _users
    val totalUsers: LiveData<Int?> = _totalUsers
    val nextCursor: LiveData<Int?> = _nextCursor
    val searchString: LiveData<String?> = _searchString

    fun getInitialUsers(searchString: String) {
        viewModelScope.launch {
            val accessToken = AuthProvider.getAccessToken()
            val result = searchRepository.searchUsers(accessToken, searchString, 0)

            if (result != null) {
                _users.postValue(result.items)
                _totalUsers.postValue(result.total)
                _nextCursor.postValue(result.nextCursor)
                _searchString.postValue(searchString)
                Log.d("SearchViewModel", "Get Initial Users")
            }
        }
    }

    fun getNextUsers() { // pagination
        if (_nextCursor.value == null) return
        viewModelScope.launch {
            val accessToken = AuthProvider.getAccessToken()
            val result = searchRepository.searchUsers(accessToken, searchString.value!!, nextCursor.value!!)
            if (result != null) {
                Log.d("SearchViewModel", "Get Next Users: offset ${nextCursor.value!!}, nextCursor ${result.nextCursor}")
                _users.postValue(users.value!! + result.items)
                _totalUsers.postValue(result.total)
                _nextCursor.postValue(result.nextCursor)
            }
        }
    }

    fun getEmptyList() {

    }
}

class SearchViewModelFactory(
    private val searchRepository: SearchRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(searchRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}