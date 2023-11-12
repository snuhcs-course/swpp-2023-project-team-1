package com.project.spire.ui.feed

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.spire.R
import com.project.spire.models.Post
import com.project.spire.ui.MainActivity
import com.project.spire.ui.search.SearchFragment
import com.project.spire.utils.DateUtils

class PostAdapter(
    private val postList: List<Post>,
    private val context: Context,
    private val activity: MainActivity
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

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
        if (post.user.profileImage == null) {
            holder.profileImage.load(R.drawable.default_profile_img) {
                transformations(CircleCropTransformation())
            }
        } else {
            holder.profileImage.load(post.user.profileImage) {
                transformations(CircleCropTransformation())
            }
        }
        holder.postImage.load(post.imageUrl)
        holder.username.text = post.user.userName
        holder.content.text = post.content
        holder.updatedAt.text = DateUtils.formatTime(post.updatedAt)
        holder.likes.text = post.likeCount.toString()
        holder.comments.text = post.commentCount.toString()

        holder.profileImage.setOnClickListener {
            // TODO: Show user profile
        }

        holder.username.setOnClickListener {
            // TODO: Show user profile
        }

        holder.content.setOnClickListener {
            activity.replaceFragment(PostFragment())
        }

        holder.comments.setOnClickListener {

        }

        holder.commentCount.setOnClickListener {

        }

        holder.likes.setOnClickListener {
            // TODO: Like post
        }

        holder.likeCount.setOnClickListener {
            // TODO: Show who liked the post
        }
    }

    inner class PostViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = view.findViewById(R.id.profile_Image)
        val postImage: ImageView = view.findViewById(R.id.post_image)
        val username: TextView = view.findViewById(R.id.username)
        val likes: TextView = view.findViewById(R.id.num_likes)
        val comments: TextView = view.findViewById(R.id.num_comments)
        val content: TextView = view.findViewById(R.id.content)
        val updatedAt: TextView = view.findViewById(R.id.updated_at)
        val likeCount: TextView = view.findViewById(R.id.num_likes)
        val commentCount: TextView = view.findViewById(R.id.num_comments)
    }
}