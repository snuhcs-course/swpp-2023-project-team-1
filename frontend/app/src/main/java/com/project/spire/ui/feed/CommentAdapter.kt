package com.project.spire.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.spire.R
import com.project.spire.models.Comment
import com.project.spire.utils.DateUtils

class CommentAdapter(
    private val commentList: List<Comment>
): RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.comment_item, parent, false)

        return CommentViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentList[position]

        if (comment.user.profileImage == null) {
            holder.profileImage.load(R.drawable.default_profile_img) {
                transformations(CircleCropTransformation())
            }
        } else {
            holder.profileImage.load(comment.user.profileImage) {
                transformations(CircleCropTransformation())
            }
        }
        holder.username.text = comment.user.userName
        holder.content.text = comment.content
        holder.updatedAt.text = DateUtils.formatTime(comment.updatedAt)
    }

    inner class CommentViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var profileImage: ImageView = view.findViewById(R.id.comment_profile_image)
        var username: TextView = view.findViewById(R.id.comment_username)
        var content: TextView = view.findViewById(R.id.comment_content)
        var updatedAt: TextView = view.findViewById(R.id.comment_updated_at)
    }
}