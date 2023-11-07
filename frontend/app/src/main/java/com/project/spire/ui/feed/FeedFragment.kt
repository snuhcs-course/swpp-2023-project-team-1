package com.project.spire.ui.feed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spire.databinding.FragmentFeedBinding

class FeedFragment : Fragment() {
    private var _binding: FragmentFeedBinding? = null
    private var postAdapter: PostAdapter? = null

    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val feedViewModel = ViewModelProvider(this)[FeedViewModel::class.java]

        feedViewModel.fetchPosts()

        binding.appBar.setOutlineProvider(null);

        recyclerView = binding.recyclerViewFeed
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true

        recyclerView.layoutManager = linearLayoutManager
        postAdapter = context?.let { PostAdapter(feedViewModel.posts.value!!) }

        feedViewModel.posts.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.shimmerViewContainer.stopShimmer()
                binding.shimmerViewContainer.visibility = View.GONE
                recyclerView.adapter = PostAdapter(it)
            }
            postAdapter?.notifyDataSetChanged()
        }
        recyclerView.adapter = postAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}