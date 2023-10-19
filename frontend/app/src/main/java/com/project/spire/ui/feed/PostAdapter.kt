package com.project.spire.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.example.spire.R
import com.project.spire.models.Post

class PostAdapter(
    private val postList: List<Post>
): RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

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
        holder.postImage.load(post.imageUrl){
            placeholder(R.drawable.logo_empty)
        }
        holder.profileImage.load(post.user.profileImage){
            transformations(CircleCropTransformation())
            placeholder(R.drawable.logo_black)
        }
        holder.username.text = post.user.userName
        holder.content.text = post.content
        holder.updatedAt.text = post.updatedAt
        holder.likes.text = post.likedUsers.size.toString()
        holder.comments.text = post.comments.size.toString()
    }


    inner class PostViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var profileImage: ImageView
        var postImage: ImageView
        var username: TextView
        var likes: TextView
        var comments: TextView
        var content: TextView
        var updatedAt: TextView

        init {
            profileImage = view.findViewById(R.id.profile_Image)
            postImage = view.findViewById(R.id.post_image)
            username = view.findViewById(R.id.username)
            likes = view.findViewById(R.id.num_likes)
            comments = view.findViewById(R.id.num_comments)
            content = view.findViewById(R.id.content)
            updatedAt = view.findViewById(R.id.updated_at)
        }
    }


}