package com.project.spire.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.transform.CircleCropTransformation
import com.example.spire.R
import com.example.spire.databinding.FragmentProfileBinding
import com.project.spire.core.auth.AuthRepository
import com.project.spire.core.auth.authDataStore
import com.project.spire.core.user.UserRepository
import com.project.spire.ui.auth.LoginActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    private val binding get() = _binding!!

    private lateinit var profileViewModel: ProfileViewModel

    private var TEST_USER_ID = "d2fcfe21-82fa-4008-835d-16c39eca26d7"//"92142569-d579-44e7-bf06-102770db6eb4"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val authRepository = AuthRepository(requireContext().authDataStore)
        val userRepository = UserRepository()
        val viewModelFactory = ProfileViewModelFactory(authRepository, userRepository)
        profileViewModel = ViewModelProvider(this, viewModelFactory)[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // If bundle is null, fetch my profile
        // Else, fetch other user's profile using bundle's user id
        if (savedInstanceState?.getString("userId") == null) {
            profileViewModel.getMyInfo()
        } else {
            val userId = savedInstanceState.getString("userId")
            profileViewModel.getUserInfo(userId!!)
        }

        profileViewModel.username.observe(viewLifecycleOwner) {
            val username = "@$it"
            binding.profileUsername.text = username
        }

        profileViewModel.bio.observe(viewLifecycleOwner) {
            binding.profileBio.text = it
        }

        profileViewModel.profileImageUrl.observe(viewLifecycleOwner) {
            binding.profileImage.load(it) {
                transformations(CircleCropTransformation())
            }
        }

        profileViewModel.followers.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.followerButton.text = "$it ${resources.getString(R.string.profile_followers)}"
            }
        }

        profileViewModel.following.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.followingButton.text = "$it ${resources.getString(R.string.profile_following)}"
            }
        }

        val largeButton = binding.profileLargeButton
        profileViewModel.isMyProfile.observe(viewLifecycleOwner) { it ->
            if (it) {
                largeButton.text = getString(R.string.profile_edit_btn)
                largeButton.background = resources.getDrawable(R.drawable.bg_profile_btn_grey, null)
                largeButton.setTextColor(resources.getColor(R.color.grey_600, null))
            } else {
                profileViewModel.followingState.observe(viewLifecycleOwner) { it->
                    if (it == -1) {
                        largeButton.text = getString(R.string.profile_follow)
                        largeButton.background = resources.getDrawable(R.drawable.bg_profile_btn_blue, null)
                        largeButton.setTextColor(resources.getColor(R.color.blue_500, null))
                    }
                    else if (it == 0) {
                        largeButton.text = getString(R.string.profile_follow_requested)
                        largeButton.background = resources.getDrawable(R.drawable.bg_profile_btn_grey, null)
                        largeButton.setTextColor(resources.getColor(R.color.grey_600, null))
                    }
                    else {
                        largeButton.text = getString(R.string.profile_following)
                        largeButton.background = resources.getDrawable(R.drawable.bg_profile_btn_grey, null)
                        largeButton.setTextColor(resources.getColor(R.color.grey_600, null))
                    }
                }
            }
        }

        binding.profileLargeButton.setOnClickListener {
            if (profileViewModel.isMyProfile.value == true) {
                startActivity(Intent(requireContext(), EditProfileActivity::class.java))
            } else {
                // TODO: Follow user
                profileViewModel.followRequest(null)
            }
        }

        binding.testButton.setOnClickListener {
            if (profileViewModel.isMyProfile.value == true) {
                profileViewModel.getUserInfo(TEST_USER_ID)
                binding.testButton.text = "My Profile"
            } else {
                profileViewModel.getMyInfo()
                binding.testButton.text = "Test Profile"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}