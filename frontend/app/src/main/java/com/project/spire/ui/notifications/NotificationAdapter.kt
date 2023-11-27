package com.project.spire.ui.notifications

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spire.R
import com.project.spire.models.Noti

class NotificationAdapter(
    var notificationList: List<Noti>
) : RecyclerView.Adapter<NotificationViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(viewType, parent, false)
        Log.d("NotificationAdapter", "viewType: $viewType")
        return when (viewType) {
            R.layout.notification_item_post -> {
                NotificationViewHolder.PostNotificationViewHolder(view)
            }
            else -> {
                NotificationViewHolder.FollowNotificationViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notificationList[position])
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun getItemViewType(position: Int): Int = when (notificationList[position].type) {
        "NEW_POST_LIKE", "NEW_COMMENT", "NEW_COMMENT_LIKE" -> {
            R.layout.notification_item_post
        }
        else -> {
            R.layout.notification_item_follow
        }
    }
}