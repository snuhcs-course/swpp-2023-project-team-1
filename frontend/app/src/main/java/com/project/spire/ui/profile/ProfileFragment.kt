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

        val largeButton = binding.profileLargeButton
        profileViewModel.isMyProfile.observe(viewLifecycleOwner) {
            if (it) {
                largeButton.text = getString(R.string.profile_edit_btn)
                largeButton.background = resources.getDrawable(R.drawable.bg_profile_btn_grey, null)
                largeButton.setTextColor(resources.getColor(R.color.grey_600, null))
            } else {
                largeButton.text = getString(R.string.profile_follow)
                largeButton.background = resources.getDrawable(R.drawable.bg_profile_btn_blue, null)
                largeButton.setTextColor(resources.getColor(R.color.blue_500, null))
            }
        }

        binding.profileLargeButton.setOnClickListener {
            if (profileViewModel.isMyProfile.value == true) {
                startActivity(Intent(requireContext(), EditProfileActivity::class.java))
            } else {
                // TODO: Follow user
            }
        }

        // FIXME: Move this to EditProfileActivity
        val logoutBtn: Button = binding.tempLogoutButton
        logoutBtn.setOnClickListener {
            val success = profileViewModel.logout()
        }

        // FIXME: Move this to EditProfileActivity
        profileViewModel.logoutSuccess.observe(viewLifecycleOwner) {
            if (it) {
                // Logout success
                Log.d("ProfileFragment", "Logout success")
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            } else {
                // Logout failed
                Log.d("ProfileFragment", "Logout failed")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}