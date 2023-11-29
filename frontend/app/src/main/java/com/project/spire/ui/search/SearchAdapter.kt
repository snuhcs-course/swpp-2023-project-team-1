package com.project.spire.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.spire.R
import com.project.spire.models.UserListItem
import com.project.spire.utils.AuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchAdapter(
    private var searchList: List<UserListItem>,
    private val navController: NavController,
): RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.user_list_item, parent, false)

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
        } else {
            holder.profileImage.load(search.profileImageUrl) {
                transformations(CircleCropTransformation())
            }
        }
        holder.username.text = search.username
        if (search.isFollowing) {
            holder.followingStatus.text = "following"
        } else {
            holder.followingStatus.text = "not following"
        }

        holder.view.setOnClickListener {
            Log.d("SearchAdapter", "Clicked on user: ${search.username}")
            showProfile(search.userId)
        }
    }

    fun updateList(newList: List<UserListItem>) {
        searchList = newList
        notifyItemInserted(searchList.size - 1)
    }

    inner class SearchViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        var profileImage: ImageView = view.findViewById(R.id.search_profile_image)
        var username: TextView = view.findViewById(R.id.search_username)
        var followingStatus: TextView = view.findViewById(R.id.user_following_status)
    }

    private fun showProfile(userId: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val myUserId = AuthProvider.getMyUserId()
            if (myUserId == userId) {
                navController.navigate(
                    R.id.action_search_to_profile
                )
            } else {
                val bundle = Bundle()
                bundle.putString("userId", userId)
                navController.navigate(
                    R.id.action_search_to_profile,
                    bundle
                )
            }
        }
    }
}