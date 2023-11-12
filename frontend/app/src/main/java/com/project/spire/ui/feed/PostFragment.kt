package com.project.spire.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spire.databinding.FragmentPostBinding

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
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        postViewModel = ViewModelProvider(this)[PostViewModel::class.java]

        val postView = binding.post
        val backButton = binding.backButton
        recyclerView = binding.recyclerViewComments

        postViewModel.loadPost()

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true

        recyclerView.layoutManager = linearLayoutManager

//        recyclerView.adapter = commentAdapter

    }
}