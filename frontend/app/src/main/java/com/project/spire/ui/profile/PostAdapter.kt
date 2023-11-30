package com.project.spire.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.spire.R
import com.project.spire.models.Post

class PostAdapter(
    private var postList: List<Post>,
    private val navController: NavController
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.post_on_profile_item, parent, false)
        return PostViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = postList[position]
        holder.image.load(item.imageUrl)
        holder.image.setOnClickListener {
            showPost(item.postId)
        }
    }

    inner class PostViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.post_image_on_profile)
    }
    private fun showPost(postId: String) {
        val bundle = Bundle()
        bundle.putString("postId", postId)
        navController.navigate(
            R.id.action_profile_to_post, // TODO
            bundle
        )
    }

    fun updatePosts(newList: List<Post>) {
        postList = newList
        notifyDataSetChanged()
    }
}