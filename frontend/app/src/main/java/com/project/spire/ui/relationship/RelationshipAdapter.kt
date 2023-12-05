package com.project.spire.ui.relationship

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
import com.project.spire.network.user.response.FollowItems

class RelationshipAdapter(
    private var relationshipList: MutableList<FollowItems>,
    private val navController: NavController
): RecyclerView.Adapter<RelationshipAdapter.FollowViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.follow_item, parent, false)

        return FollowViewHolder(layout)
    }

    override fun onBindViewHolder(holder: FollowViewHolder, position: Int) {
        val user = relationshipList[position]
        holder.username.text = user.username
        if (user.profileImageUrl == null) {
            holder.profileImage.load(R.drawable.default_profile_img) {
                transformations(CircleCropTransformation())
            }
        } else {
            holder.profileImage.load(user.profileImageUrl) {
                transformations(CircleCropTransformation())
            }
        }
    }

    override fun getItemCount(): Int {
        return relationshipList.size
    }

    fun updateList(list: List<FollowItems>) {
        val previousSize = itemCount
        relationshipList = list.toMutableList()
        notifyItemRangeInserted(previousSize, list.size)
    }

    inner class FollowViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = view.findViewById(R.id.profile_image)
        val username: TextView = view.findViewById(R.id.username)

        init {
            view.setOnClickListener {
                showProfile(relationshipList[adapterPosition].id)
            }
        }
    }

    private fun showProfile(userId: String) {
        Log.d("FeedAdapter", "Showing profile of user $userId")
        navController.navigate(
            R.id.action_relationship_to_profile,
            Bundle().apply {
                putString("userId", userId)
            }
        )
    }
}
