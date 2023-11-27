package com.project.spire.ui.notifications

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.spire.R
import com.project.spire.models.Noti
import com.project.spire.utils.DateUtils

open class NotificationViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(noti: Noti) {
        // Set UI for common notification fields
        val profileImage = view.findViewById<ImageView>(R.id.notification_profile_image)
        val userName = view.findViewById<TextView>(R.id.notification_username)
        val time = view.findViewById<TextView>(R.id.notification_time)
        profileImage.load(noti.sender.profileImage) {
            crossfade(true)
            placeholder(R.drawable.default_profile_img)
            transformations(CircleCropTransformation())
        }
        userName.text = noti.sender.userName
        time.text = DateUtils.formatTime(noti.createdAt)
    }

    class PostNotificationViewHolder(private val view: View) : NotificationViewHolder(view) {
        override fun bind(noti: Noti) {
            super.bind(noti)
            val content = view.findViewById<TextView>(R.id.notification_text)
            val postImage = view.findViewById<ImageView>(R.id.notification_post_image)
            content.text = when (noti.type) {
                "NEW_POST_LIKE" -> {
                    view.context.getString(R.string.notifications_liked_your_post)
                }
                "NEW_COMMENT" -> {
                    view.context.getString(R.string.notifications_commented_your_post)
                }
                "NEW_COMMENT_LIKE" -> {
                    view.context.getString(R.string.notifications_liked_your_comment)
                }
                else -> {
                    ""
                }
            }
            /* TODO
            postImage.load(noti.postImageUrl) {
                crossfade(true)
                transformations(CircleCropTransformation())
            }
            */
        }
    }

    class FollowNotificationViewHolder(private val view: View) : NotificationViewHolder(view) {
        override fun bind(noti: Noti) {
            super.bind(noti)
            val content = view.findViewById<TextView>(R.id.notification_text)
            val acceptBtn = view.findViewById<TextView>(R.id.follow_accept_btn)
            val declineBtn = view.findViewById<TextView>(R.id.follow_decline_btn)
            content.text = when (noti.type) {
                "FOLLOW_REQUEST" -> {
                    view.context.getString(R.string.notifications_follow_request)
                }
                "FOLLOW_ACCEPT" -> {
                    acceptBtn.visibility = View.GONE
                    declineBtn.visibility = View.GONE
                    view.context.getString(R.string.notifications_follow_accept)
                }
                else -> {
                    ""
                }
            }
            acceptBtn.setOnClickListener {
                // TODO
            }
            declineBtn.setOnClickListener {
                // TODO
            }
        }
    }
}