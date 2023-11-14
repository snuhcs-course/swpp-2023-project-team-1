package com.project.spire.ui.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.spire.R
import com.example.spire.databinding.FragmentPostBinding
import com.project.spire.models.Comment
import com.project.spire.models.Post
import com.project.spire.utils.DateUtils

class PostFragment : Fragment() {
    private var _binding: FragmentPostBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var postViewModel: PostViewModel

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("PostFragment", "onCreateView with postId: ${arguments?.getString("postId")}")
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postViewModel = ViewModelProvider(this)[PostViewModel::class.java]

        val postView = binding.post
        val backButton = binding.backButton
        val commentButton = binding.commentWriteBtn

        postView.postFooter.visibility = View.INVISIBLE
        recyclerView = binding.recyclerViewComments

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = false
        linearLayoutManager.stackFromEnd = false
        recyclerView.layoutManager = linearLayoutManager

        postViewModel.loadPost(arguments?.getString("postId")!!)

        postViewModel.post.observe(viewLifecycleOwner) {
            // Post loaded
            if (it != null) {
                postViewModel.loadInitialComments()
                onPostLoaded(it)
            }
        }

        postViewModel.comments.observe(viewLifecycleOwner) {
            // Comments loaded
            if (it != null) {
                onCommentsLoaded(it)
            }
        }

        commentButton.setOnClickListener {
            commentButton.visibility = View.GONE
            binding.commentWriteProgressBar.visibility = View.VISIBLE
            val commentContent = binding.commentWriteEditText.text.toString()
            postViewModel.comment(commentContent)
        }

        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            postViewModel.loadPost(arguments?.getString("postId")!!)
            postViewModel.loadInitialComments()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun onPostLoaded(post: Post) {
        val postView = binding.post

        if (post.user.profileImage == null) {
            postView.profileImage.load(R.drawable.default_profile_img) {
                transformations(CircleCropTransformation())
            }
        } else {
            postView.profileImage.load(post.user.profileImage) {
                transformations(CircleCropTransformation())
            }
        }
        postView.postImage.load(post.imageUrl)
        postView.username.text = post.user.userName
        postView.content.text = post.content
        postView.updatedAt.text = DateUtils.formatTime(post.updatedAt)
        postView.numLikes.text = post.likeCount.toString()
        postView.numComments.text = post.commentCount.toString()

        binding.commentWriteLayout.visibility = View.VISIBLE
        postView.postFooter.visibility = View.VISIBLE

        binding.shimmerViewContainer.stopShimmer()
        binding.shimmerViewContainer.visibility = View.GONE
    }

    private fun onCommentsLoaded(comments: List<Comment>) {
        binding.commentWriteBtn.visibility = View.VISIBLE
        binding.commentWriteProgressBar.visibility = View.GONE
        binding.commentWriteEditText.text?.clear()
        val commentAdapter = CommentAdapter(comments)
        recyclerView.adapter = commentAdapter
        recyclerView.visibility = View.VISIBLE
        binding.shimmerViewContainerComment.stopShimmer()
        binding.shimmerViewContainerComment.visibility = View.GONE
    }
}