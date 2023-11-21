package com.project.spire.ui.search

import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spire.R
import com.example.spire.databinding.FragmentSearchBinding
import com.google.android.material.textfield.TextInputEditText
import com.project.spire.core.search.SearchRepository
import com.project.spire.ui.feed.FeedAdapter
import com.project.spire.ui.profile.ProfileViewModelFactory

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var text: TextInputEditText
    private lateinit var searchViewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = FragmentSearchBinding.inflate(inflater, container, false)
            val searchRepository = SearchRepository()
            val viewModelFactory = SearchViewModelFactory(searchRepository)
            searchViewModel = ViewModelProvider(this, viewModelFactory)[SearchViewModel::class.java]
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.searchRecyclerView
        text = binding.searchBarEditText


        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = false
        linearLayoutManager.stackFromEnd = false

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.addOnChildAttachStateChangeListener(onChildAttachStateChangeListener)
        val adapter = SearchAdapter(emptyList(), findNavController())
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // Loads more posts when the user scrolls to the bottom of the list
                if (!recyclerView.canScrollVertically(1)) {
                    searchViewModel.getNextUsers()
                    searchViewModel.users.observe(viewLifecycleOwner) {
                        recyclerView.run {
                            adapter.updateList(it)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        })

        text.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                text.hint = ""
            } else {
                text.hint = R.string.search_bar_hint.toString()
            }
        }

        text.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.d("SearchFragment", "Text changed: ${s.toString()}")
                //if (s.toString() != "")
                searchViewModel.getInitialUsers(s.toString())
                searchViewModel.users.observe(viewLifecycleOwner) {
                    //if (it.isNotEmpty())
                    recyclerView.run {
                        adapter.updateList(it)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // do nothing
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // do nothing
            }
        })
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