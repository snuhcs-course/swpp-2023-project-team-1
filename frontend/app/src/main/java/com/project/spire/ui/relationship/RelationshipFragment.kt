package com.project.spire.ui.relationship

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.spire.R
import com.example.spire.databinding.FragmentRelationshipBinding

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
    }
}