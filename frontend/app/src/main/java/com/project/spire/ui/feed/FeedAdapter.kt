package com.project.spire.ui.feed

import android.annotation.SuppressLint
import android.app.Notification.Action
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.spire.R
import com.project.spire.models.Post
import com.project.spire.utils.DateUtils

class FeedAdapter(
    private var postList: List<Post>,
    private val navController: NavController,
    private val feedViewModel: FeedViewModel
) : RecyclerView.Adapter<FeedAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.post_item, parent, false)

        return PostViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        handleUI(holder, post)

        holder.profileImage.setOnClickListener {
            showProfile(post.user.id)
        }
        holder.username.setOnClickListener {
            showProfile(post.user.id)
        }
        holder.postImage.setOnClickListener {
            showPost(post.postId)
        }
        holder.content.setOnClickListener {
            showPost(post.postId)
        }
        holder.comments.setOnClickListener {
            showPost(post.postId)
        }
        holder.commentCount.setOnClickListener {
            showPost(post.postId)
        }
        holder.likes.setOnClickListener {
            feedViewModel.likePost(position)
        }
        holder.likeCount.setOnClickListener {
            // TODO: Show who liked the post
        }
    }

    inner class PostViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = view.findViewById(R.id.profile_Image)
        val postImage: ImageView = view.findViewById(R.id.post_image)
        val originalImage: ImageView = view.findViewById(R.id.original_image)
        val originalImageButton: LinearLayout = view.findViewById(R.id.original_image_btn)
        val username: TextView = view.findViewById(R.id.username)
        val likes: ImageView = view.findViewById(R.id.post_image_like_btn)
        val comments: ImageView = view.findViewById(R.id.post_image_comment_btn)
        val content: TextView = view.findViewById(R.id.content)
        val updatedAt: TextView = view.findViewById(R.id.updated_at)
        val likeCount: TextView = view.findViewById(R.id.num_likes)
        val commentCount: TextView = view.findViewById(R.id.num_comments)
    }

    private fun showPost(postId: String) {
        val bundle = Bundle()
        bundle.putString("postId", postId)
        navController.navigate(
            R.id.action_feed_to_post,
            bundle
        )
    }

    private fun showProfile(userId: String) {
        if (feedViewModel.myUserId.value!! == userId) {
            navController.navigate(
                R.id.action_feed_to_profile
            )
        } else {
            val bundle = Bundle()
            bundle.putString("userId", userId)
            navController.navigate(
                R.id.action_feed_to_profile,
                bundle
            )
        }
    }

    fun updateList(newList: List<Post>) {
        val previousItemCount = itemCount
        postList = newList
        notifyItemRangeInserted(previousItemCount, newList.size)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun handleUI(holder: PostViewHolder, post: Post) {
        if (post.user.profileImage == null) {
            holder.profileImage.load(R.drawable.default_profile_img) {
                transformations(CircleCropTransformation())
            }
        } else {
            holder.profileImage.load(post.user.profileImage) {
                transformations(CircleCropTransformation())
            }
        }

        if (post.isLiked == 1) {
            holder.likes.setImageResource(R.drawable.like_filled)
        } else {
            holder.likes.setImageResource(R.drawable.like)
        }

        holder.postImage.load(post.imageUrl)
        holder.originalImage.load(post.originalImageUrl)
        holder.username.text = post.user.userName
        holder.content.text = post.content
        holder.updatedAt.text = DateUtils.formatTime(post.updatedAt)
        holder.likeCount.text = post.likeCount.toString()
        holder.commentCount.text = post.commentCount.toString()

        if (post.originalImageUrl != null) {
            Log.d("FeedAdapter", "Original image url: ${post.originalImageUrl}")
            holder.originalImageButton.visibility = View.VISIBLE
        } else {
            holder.originalImageButton.visibility = View.GONE
        }

        holder.originalImageButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    holder.originalImage.visibility = View.VISIBLE
                    holder.postImage.visibility = View.INVISIBLE
                }

                MotionEvent.ACTION_UP -> {
                    holder.originalImage.visibility = View.INVISIBLE
                    holder.postImage.visibility = View.VISIBLE
                }
            }
            true
        }
    }
}