package com.project.spire.ui.notifications

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.spire.models.Noti
import com.project.spire.models.User
import com.project.spire.network.RetrofitClient
import com.project.spire.utils.AuthProvider
import kotlinx.coroutines.launch

const val LIMIT = 10

class NotificationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Nothing here yet!"
    }
    private val _notifications = MutableLiveData<List<Noti>>().apply {
        value = emptyList()
    }
    private val _total = MutableLiveData<Int>().apply {
        value = 0
    }
    private val _nextCursor = MutableLiveData<Int?>().apply {
        value = 0
    }
    val text: LiveData<String> = _text
    val notifications: LiveData<List<Noti>> = _notifications

    fun getInitialNotifications() {
        if (_nextCursor.value != null) {
            viewModelScope.launch {
                val accessToken = AuthProvider.getAccessToken()
                val response = RetrofitClient.notificationAPI.getMyNotifications(
                    accessToken = accessToken,
                    limit = LIMIT,
                    offset = 0
                )
                if (response.isSuccessful && response.body() != null && response.code() == 200) {
                    Log.i("NotificationsViewModel", "Success: ${response.body()}")
                    val body = response.body()!!
                    _notifications.value = body.notifications
                    _total.value = body.total
                    _nextCursor.value = body.nextCursor
                } else {
                    // TODO: Handle error
                    Log.e(
                        "NotificationsViewModel",
                        "Error: ${response.code()}\n${response.errorBody()}"
                    )
                }
            }
        }
    }

    fun getMoreNotifications() {
        if (_nextCursor.value != null) {
            viewModelScope.launch {
                val accessToken = AuthProvider.getAccessToken()
                val response = RetrofitClient.notificationAPI.getMyNotifications(
                    accessToken = accessToken,
                    limit = LIMIT,
                    offset = _nextCursor.value!!
                )
                if (response.isSuccessful && response.body() != null && response.code() == 200) {
                    Log.i("NotificationsViewModel", "Success: ${response.body()}")
                    val body = response.body()!!
                    _notifications.value = _notifications.value!! + body.notifications
                    _total.value = body.total
                    _nextCursor.value = body.nextCursor
                } else {
                    // TODO: Handle error
                    Log.e(
                        "NotificationsViewModel",
                        "Error: ${response.code()}\n${response.errorBody()}"
                    )
                }
            }
        }
    }
}