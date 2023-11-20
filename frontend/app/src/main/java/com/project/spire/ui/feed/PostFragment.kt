package com.project.spire.ui.feed

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.spire.R
import com.example.spire.databinding.FragmentPostBinding
import com.project.spire.models.Comment
import com.project.spire.models.Post
import com.project.spire.ui.profile.ProfileFragment
import com.project.spire.utils.DateUtils

class PostFragment : Fragment() {
    private var _binding: FragmentPostBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var postViewModel: PostViewModel

    private var onEdit: Boolean = false
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postViewModel = ViewModelProvider(this)[PostViewModel::class.java]

        val postView = binding.post
        val backButton = binding.backButton
        val commentButton = binding.commentWriteBtn
        val originalImageBtn = binding.post.originalImageBtn
        val myProfileImage = binding.commentWriteProfileImage

        postView.postFooter.visibility = View.INVISIBLE
        recyclerView = binding.recyclerViewComments

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = false
        linearLayoutManager.stackFromEnd = false
        recyclerView.layoutManager = linearLayoutManager

        postViewModel.loadPost(arguments?.getString("postId")!!)
        postViewModel.loadMyInfo()

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

        postViewModel.myProfileImage.observe(viewLifecycleOwner) {
            if (it != null) {
                myProfileImage.load(it) {
                    transformations(CircleCropTransformation())
                }
            }
        }

        myProfileImage.load(R.drawable.default_profile_img) {
            transformations(CircleCropTransformation())
        }

        postView.profileImage.setOnClickListener {
            showProfile(postViewModel.post.value!!.user.id)
        }

        postView.username.setOnClickListener {
            showProfile(postViewModel.post.value!!.user.id)
        }

        postView.postImageLikeBtn.setOnClickListener {
            postViewModel.likePost()
        }

        postView.editPostBtn.setOnClickListener {
            onEdit = if (onEdit) {
                cancelEditPost()
                false
            } else {
                onEditPost()
                true
            }
        }

        postView.deletePostBtn.setOnClickListener {
            onDeletePost()
        }

        postView.postEditSaveBtn.setOnClickListener {
            onSavePost()
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

        originalImageBtn.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    postView.originalImage.visibility = View.VISIBLE
                    postView.postImage.visibility = View.INVISIBLE
                }
                MotionEvent.ACTION_UP -> {
                    postView.originalImage.visibility = View.INVISIBLE
                    postView.postImage.visibility = View.VISIBLE
                }
            }
            true
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

        if (post.originalImageUrl != null) {
            postView.originalImageBtn.visibility = View.VISIBLE
        }

        if (post.isLiked == 1) {
            postView.postImageLikeBtn.setImageResource(R.drawable.like_filled)
        } else {
            postView.postImageLikeBtn.setImageResource(R.drawable.like)
        }

        if (post.user.id == postViewModel.myUserId.value) {
            postView.editPostBtn.visibility = View.VISIBLE
            postView.deletePostBtn.visibility = View.VISIBLE
        }

        postView.postImage.load(post.imageUrl)
        postView.originalImage.load(post.originalImageUrl)
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
        val commentAdapter = CommentAdapter(comments, findNavController(), postViewModel)
        recyclerView.adapter = commentAdapter
        recyclerView.visibility = View.VISIBLE
        binding.shimmerViewContainerComment.stopShimmer()
        binding.shimmerViewContainerComment.visibility = View.GONE
    }

    private fun onEditPost() {
        val postView = binding.post
        postView.editContentLayout.visibility = View.VISIBLE
        postView.content.visibility = View.GONE
        postView.editContentLayout.editText?.setText(postView.content.text)
        postView.editPostBtn.setImageResource(R.drawable.ic_cancel)
        postView.postEditSaveBtn.visibility = View.VISIBLE
    }

    private fun cancelEditPost() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cancel Edit")
            .setMessage("Your changes will not be saved.")
            .setPositiveButton("Yes") { _, _ ->
                val postView = binding.post
                postView.editContentLayout.visibility = View.GONE
                postView.content.visibility = View.VISIBLE
                postView.editPostBtn.setImageResource(R.drawable.ic_edit_post)
                postView.postEditSaveBtn.visibility = View.GONE
            }
            .setNegativeButton("No") { _, _ -> }
            .show()
    }

    private fun onSavePost() {
        AlertDialog.Builder(requireContext())
            .setTitle("Save Post")
            .setMessage("Are you sure you want to save this post?")
            .setPositiveButton("Yes") { _, _ ->
                val postView = binding.post
                val content = postView.editContentLayout.editText?.text.toString()
                postViewModel.editPost(content)
                popToFeed()
            }
            .setNegativeButton("No") { _, _ -> }
            .show()
    }

    private fun onDeletePost() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Post")
            .setMessage("Are you sure you want to delete this post?")
            .setPositiveButton("Yes") { _, _ ->
                postViewModel.deletePost()
                popToFeed()
            }
            .setNegativeButton("No") { _, _ -> }
            .show()
    }

    private fun popToFeed() {
        findNavController().popBackStack(R.id.tab_feed, false)
        findNavController().navigate(R.id.tab_feed)
    }

    private fun showProfile(userId: String) {
        if (postViewModel.myUserId.value == userId) {
            findNavController().navigate(
                R.id.action_post_to_profile
            )
        } else {
            val bundle = Bundle()
            bundle.putString("userId", userId)
            findNavController().navigate(
                R.id.action_post_to_profile,
                bundle
            )
        }
    }
}