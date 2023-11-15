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
import com.project.spire.models.SearchUser

class SearchAdapter(
    private val searchList: List<SearchUser>
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

        holder.profileImage.load(search.profileImage){
            transformations(CircleCropTransformation())
            placeholder(R.drawable.logo_black)
        }
        holder.username.text = search.username
        if (search.isFollowing) {
            holder.followingStatus.text = "following"
        } else {
            holder.followingStatus.text = "not following"
        }
    }

    inner class SearchViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var profileImage: ImageView = view.findViewById(R.id.search_profile_image)
        var username: TextView = view.findViewById(R.id.search_username)
        var followingStatus: TextView = view.findViewById(R.id.user_following_status)
    }
}