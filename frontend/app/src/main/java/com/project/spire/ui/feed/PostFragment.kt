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
import com.project.spire.utils.DateUtils

class PostFragment : Fragment() {
    private var _binding: FragmentPostBinding? = null
//    private lateinit var commentAdapter: CommentAdapter
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
        val commentWriteLayout = binding.commentWriteLayout

        postView.postFooter.visibility = View.INVISIBLE

        recyclerView = binding.recyclerViewComments

        postViewModel.loadPost(arguments?.getString("postId")!!)

        postViewModel.post.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.user.profileImage == null) {
                    postView.profileImage.load(R.drawable.default_profile_img) {
                        transformations(CircleCropTransformation())
                    }
                } else {
                    postView.profileImage.load(it.user.profileImage) {
                        transformations(CircleCropTransformation())
                    }
                }
                postView.postImage.load(it.imageUrl)
                postView.username.text = it.user.userName
                postView.content.text = it.content
                postView.updatedAt.text = DateUtils.formatTime(it.updatedAt)
                postView.numLikes.text = it.likeCount.toString()
                postView.numComments.text = it.commentCount.toString()

                commentWriteLayout.visibility = View.VISIBLE
                postView.postFooter.visibility = View.VISIBLE

                binding.shimmerViewContainer.stopShimmer()
            }
        }

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true

        recyclerView.layoutManager = linearLayoutManager

//        recyclerView.adapter = commentAdapter

    }
}