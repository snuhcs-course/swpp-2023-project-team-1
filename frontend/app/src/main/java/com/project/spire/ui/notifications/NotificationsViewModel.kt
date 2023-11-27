package com.project.spire.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.spire.models.Noti
import com.project.spire.models.User

class NotificationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Nothing here yet!"
    }
    private val _notifications = MutableLiveData<List<Noti>>().apply {
        value = getDummyNotifications()
    }
    val text: LiveData<String> = _text
    val notifications: LiveData<List<Noti>> = _notifications


    private fun getDummyNotifications(): List<Noti> {
        // Noti 1
        val noti1 = Noti(
            id = "1",
            type = "NEW_POST_LIKE",
            readAt = null,
            senderId = "1",
            sender = User(
                id = "1",
                userName = "johndoe",
                profileImage = "https://i.pravatar.cc/150?img=1"
            ),
            recipientId = "2",
            recipient = User(
                id = "2",
                userName = "janedoe",
                profileImage = "https://i.pravatar.cc/150?img=2"
            ),
            postId = "1",
            createdAt = "2021-08-01T00:00:00.000000Z",
            updatedAt = "2021-08-01T00:00:00.000000Z"
        )
        // Noti 2
        val noti2 = Noti(
            id = "2",
            type = "FOLLOW_REQUEST",
            readAt = null,
            senderId = "1",
            sender = User(
                id = "1",
                userName = "johndoe",
                profileImage = "https://i.pravatar.cc/150?img=1"
            ),
            recipientId = "2",
            recipient = User(
                id = "2",
                userName = "janedoe",
                profileImage = "https://i.pravatar.cc/150?img=2"
            ),
            postId = "1",
            createdAt = "2021-08-01T00:00:00.000000Z",
            updatedAt = "2021-08-01T00:00:00.000000Z"
        )
        return listOf(noti1, noti2)
    }
}