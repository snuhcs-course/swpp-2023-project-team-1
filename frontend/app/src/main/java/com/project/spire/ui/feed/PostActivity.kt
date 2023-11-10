package com.project.spire.ui.feed

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.spire.R
import com.example.spire.databinding.ActivityMainBinding
import com.example.spire.databinding.ActivityPostBinding
import com.project.spire.models.Comment
import com.project.spire.models.Post
import com.project.spire.models.User

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val postView = binding.post
//        val postData = fetchPost()
//
//        postView.content.setText(postData.content)
//        postView.postImage.load(postData.imageUrl){
//            placeholder(R.drawable.logo_empty)
//        }
//        postView.profileImage.load(postData.user.profileImage){
//            transformations(CircleCropTransformation())
//            placeholder(R.drawable.logo_black)
//        }
//
//        postView.username.text = postData.user.userName
//        postView.content.text = postData.content
//        postView.updatedAt.text = postData.updatedAt
//        postView.numLikes.text = postData.likedUsers.size.toString()
//        postView.numComments.text = postData.comments.size.toString()
//
//        binding.commentWriteProfileImage.load(postData.user.profileImage){
//            transformations(CircleCropTransformation())
//            placeholder(R.drawable.logo_black)
//        }

        recyclerView = binding.recyclerViewComments

        this.applicationContext

        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true

        recyclerView.layoutManager = linearLayoutManager

//        commentAdapter = applicationContext.let { CommentAdapter(postData.comments) }
        recyclerView.adapter = commentAdapter

    }

//    private fun fetchPost(): Post {
//        // TODO: fetch from backend, and move to ViewModel
//
//        val user_id = "100"
//        val username = "jimin"
//        val profileImage = "https://i.pravatar.cc/1000"
//        val user = User(user_id,username,profileImage)
//        val post_id = 100
//        var content = "Testing Post Expanded Page"
//        var imageUrl = "https://i.pravatar.cc/1000"
//        var likedUsers = ArrayList<User>()
//        var comments = fetchComments()
//        val createdAt = "1 hour ago"
//        var updatedAt = "1 hour ago"
//
//        val post = Post((user_id, user, content, imageUrl, likedUsers, comments, createdAt, updatedAt))
//        return post
//    }
//
//    private fun fetchComments(): ArrayList<Comment> {
//        var commentList = ArrayList<Comment>()
//        // TODO: fetch from backend, and move to ViewModel
//        for(i in 1..10){
//            val id = "100"
//            val user_id = "100"
//            val username = "jimin"
//            val profileImage = "https://i.pravatar.cc/1000"
//            val user = User(user_id, username, profileImage)
//            var content = "This is a comment"
//            var likedUsers = ArrayList<User>()
//            val createdAt = "a minute ago"
//            var updatedAt = "a minute ago"
//            val comment = Comment(id, user, content, likedUsers, createdAt, updatedAt)
//
//            commentList.add(comment)
//        }
//        return commentList
//    }


}