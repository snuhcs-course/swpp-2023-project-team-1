package com.project.spire.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.spire.R
import com.project.spire.models.Post
import de.hdodenhof.circleimageview.CircleImageView

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
            placeholder(R.drawable.logo_black)
        }
        holder.username.text = post.user.userName
        holder.content.text = post.content
    }


    inner class PostViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var profileImage: CircleImageView
        var postImage: ImageView
        var likeButton: ImageView
        var commentButton: ImageView
        var username: TextView
        var likes: TextView
        var content: TextView
        var comments: TextView

        init {
            profileImage = view.findViewById(R.id.ProfileImage)
            postImage = view.findViewById(R.id.PostImage)
            likeButton = view.findViewById(R.id.PostImageLikeBtn)
            commentButton = view.findViewById(R.id.PostImageCommentBtn)
            username = view.findViewById(R.id.Username)
            likes = view.findViewById(R.id.NumLikes)
            content = view.findViewById(R.id.Content)
            comments = view.findViewById(R.id.Comments)
        }
    }


}