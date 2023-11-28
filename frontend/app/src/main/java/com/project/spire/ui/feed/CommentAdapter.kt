package com.project.spire.ui.feed

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.spire.R
import com.project.spire.models.Comment
import com.project.spire.utils.DateUtils

class CommentAdapter(
    private val commentList: MutableList<Comment>,
    private val navController: NavController,
    private val context: Context,
    private val postViewModel: PostViewModel
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {


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

        if (comment.user.id == postViewModel.myUserId.value) {
            holder.deleteBtn.visibility = View.VISIBLE
        }
    }

    inner class CommentViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = view.findViewById(R.id.comment_profile_image)
        val username: TextView = view.findViewById(R.id.comment_username)
        val content: TextView = view.findViewById(R.id.comment_content)
        val updatedAt: TextView = view.findViewById(R.id.comment_updated_at)
        val deleteBtn: ImageView = view.findViewById(R.id.comment_delete_btn)

        init {
            val position = adapterPosition
            val commentId = commentList[adapterPosition].id
            deleteBtn.setOnClickListener {
                deleteComment(position)
            }
            profileImage.setOnClickListener {
                showProfile(commentId)
            }
            username.setOnClickListener {
                showProfile(commentId)
            }
        }
    }

    private fun showProfile(userId: String) {
        if (postViewModel.myUserId.value == userId) {
            navController.navigate(
                R.id.action_post_to_profile,
            )
        } else {
            val bundle = Bundle()
            bundle.putString("userId", userId)
            navController.navigate(
                R.id.action_post_to_profile,
                bundle
            )
        }

    }

    private fun deleteComment(position: Int) {
        val commentId = commentList[position].id
        AlertDialog.Builder(context)
            .setTitle("Delete Comment")
            .setMessage("Are you sure you want to delete this comment?")
            .setPositiveButton("Yes") { _, _ ->
                Log.d("CommentAdapter", "Deleting comment[$position]")
                commentList.removeAt(position)
                notifyItemRemoved(position)
                postViewModel.deleteComment(commentId)
            }
            .setNegativeButton("No") { _, _ -> }
            .show()
    }
}