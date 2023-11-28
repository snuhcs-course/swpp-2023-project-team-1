package com.project.spire.ui.relationship

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spire.R
import com.example.spire.databinding.FragmentRelationshipBinding
import com.project.spire.network.user.response.FollowItems

class RelationshipFragment : Fragment() {

    private var _binding: FragmentRelationshipBinding? = null
    private lateinit var relationshipViewModel: RelationshipViewModel
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRelationshipBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        relationshipViewModel = ViewModelProvider(this)[RelationshipViewModel::class.java]
        val userId = arguments?.getString("userId")
        val type = arguments?.getString("type")

        relationshipViewModel.getRelationship(userId!!, type!!)

        when (type) {
            "followers" -> {
                binding.relationshipToolbarTitle.text = getString(R.string.relationship_followers)
            }

            "following" -> {
                binding.relationshipToolbarTitle.text = getString(R.string.relationship_following)
            }
        }

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val recyclerView = binding.relationshipRecyclerView
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = false
        linearLayoutManager.stackFromEnd = false

        recyclerView.layoutManager = linearLayoutManager
        val adapter = RelationshipAdapter(mutableListOf(), findNavController())
        recyclerView.adapter = adapter

        relationshipViewModel.users.observe(viewLifecycleOwner) {
            Log.d("RelationshipFragment", "Users: $it")
            if (!it.isNullOrEmpty()) {
                binding.relationshipEmptyText.visibility = View.GONE
                recyclerView.run {
                    adapter.updateList(it)
                }
            }
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // Loads more users when the user scrolls to the bottom of the list
                if (!recyclerView.canScrollVertically(1)) {
                    relationshipViewModel.getRelationship(userId, type)
                }
            }
        })
    }
}