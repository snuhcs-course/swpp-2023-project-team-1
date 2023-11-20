package com.project.spire.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.spire.R
import com.project.spire.models.Post
import com.project.spire.models.SearchUser

class SearchAdapter(
    private var searchList: List<SearchUser>
): RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.search_item, parent, false)

        return SearchViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val search = searchList[position]

        if (search.profileImageUrl == null) {
            holder.profileImage.load(R.drawable.default_profile_img) {
                transformations(CircleCropTransformation())
            }
        }
        else {
            holder.profileImage.load(search.profileImageUrl){
                transformations(CircleCropTransformation())
                placeholder(R.drawable.default_profile_img)
            }
        }
        holder.username.text = search.username
        if (search.isFollowing) {
            holder.followingStatus.text = "following"
        } else {
            holder.followingStatus.text = "not following"
        }
    }

    fun updateList(newList: List<SearchUser>) {
        searchList = newList
        notifyItemInserted(searchList.size - 1)
    }

    inner class SearchViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var profileImage: ImageView = view.findViewById(R.id.search_profile_image)
        var username: TextView = view.findViewById(R.id.search_username)
        var followingStatus: TextView = view.findViewById(R.id.user_following_status)
    }
}