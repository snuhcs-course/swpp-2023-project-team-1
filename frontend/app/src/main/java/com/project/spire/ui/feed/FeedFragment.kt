package com.project.spire.ui.feed

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spire.databinding.FragmentFeedBinding
import com.project.spire.utils.FeedViewModelFactory

class FeedFragment : Fragment() {
    private var _binding: FragmentFeedBinding? = null
    private lateinit var feedViewModel: FeedViewModel

    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = FragmentFeedBinding.inflate(inflater, container, false)
            val viewModelFactory = FeedViewModelFactory()
            feedViewModel = ViewModelProvider(this, viewModelFactory)[FeedViewModel::class.java]
            feedViewModel.getInitialPosts()
            Log.d("FeedFragment", "Loaded initial posts")
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recyclerView = binding.recyclerViewFeed
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = false
        linearLayoutManager.stackFromEnd = false

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.addOnChildAttachStateChangeListener(onChildAttachStateChangeListener)
        val adapter = FeedAdapter(emptyList(), findNavController(), feedViewModel)
        recyclerView.adapter = adapter

        feedViewModel.posts.observe(viewLifecycleOwner) {
            Log.i("FeedFragment", "Posts updated: ${it.size}")
            if (it.isNotEmpty()) {
                recyclerView.run {
                    adapter.updateList(it)
                    binding.shimmerViewContainer.stopShimmer()
                    binding.shimmerViewContainer.visibility = View.GONE
                }
            }
        }

        feedViewModel.postLiked.observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.notifyItemChanged(it)
            }
        }

        binding.appBar.outlineProvider = null
        binding.swipeRefreshLayout.setOnRefreshListener {
            feedViewModel.getInitialPosts()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // Loads more posts when the user scrolls to the bottom of the list
                if (feedViewModel.nextCursor.value != null && !recyclerView.canScrollVertically(1)) {
                    feedViewModel.getMorePosts()
                }
            }
        })
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