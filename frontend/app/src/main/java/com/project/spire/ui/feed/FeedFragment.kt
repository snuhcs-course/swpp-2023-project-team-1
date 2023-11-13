package com.project.spire.ui.feed

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spire.databinding.FragmentFeedBinding

class FeedFragment : Fragment() {
    private var _binding: FragmentFeedBinding? = null
    private var postAdapter: FeedAdapter? = null

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

        feedViewModel.getInitialPosts()

        binding.appBar.outlineProvider = null

        recyclerView = binding.recyclerViewFeed
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.addOnChildAttachStateChangeListener(onChildAttachStateChangeListener)

        feedViewModel.posts.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                recyclerView.run {
                    adapter = FeedAdapter(it, context, findNavController())
                    binding.shimmerViewContainer.stopShimmer()
                    binding.shimmerViewContainer.visibility = View.GONE

                }
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            feedViewModel.getInitialPosts()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        recyclerView.adapter = postAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val onChildAttachStateChangeListener = object : RecyclerView.OnChildAttachStateChangeListener {
        override fun onChildViewAttachedToWindow(view: View) {
            // Asynchronously update the RecyclerView's visibility once all child views are attached
            val isAllChildrenAttached = recyclerView.childCount == recyclerView.adapter?.itemCount
            Handler(Looper.getMainLooper()).post {
                if (isAllChildrenAttached) {
                    recyclerView.visibility = View.VISIBLE
                }
            }
        }
        override fun onChildViewDetachedFromWindow(view: View) {
            // Child view detached, no need to update visibility
        }
    }
}