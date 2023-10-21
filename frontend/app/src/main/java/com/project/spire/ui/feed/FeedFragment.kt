package com.project.spire.ui.feed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spire.databinding.FragmentFeedBinding
import com.project.spire.models.Comment
import com.project.spire.models.Post
import com.project.spire.models.User

class FeedFragment : Fragment() {
    private var _binding: FragmentFeedBinding? = null
    private var postAdapter: PostAdapter? = null
    private  var postList : MutableList<Post>? = null

    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.appBar.setOutlineProvider(null);

        recyclerView = binding.recyclerViewFeed

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true

        recyclerView.layoutManager = linearLayoutManager

        postList = ArrayList()
        postAdapter = context?.let { PostAdapter(postList as ArrayList<Post>) }
        recyclerView.adapter = postAdapter

        fetchPosts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchPosts() {
        //TODO implement this
        // fetch posts from backend
        // For post in posts
        // postList!!.add(post)


        //TODO Below should be throw away

        for(i in 1..10){
            val user_id=345
            val username = "ezhun"
            val profileImage = "https://i.pravatar.cc/1000"
            val user= User(user_id,username,profileImage)
            val post_id=123
            var content="This is a post"
            var imageUrl="https://i.pravatar.cc/1000"
            var likedUsers=ArrayList<User>()
            var comments=ArrayList<Comment>()
            val createdAt="1 hours ago"
            var updatedAt="1 hours ago"

            val post = Post(user_id,user,content,imageUrl,likedUsers,comments,createdAt,updatedAt)

            postList!!.add(post)
        }
    }
}