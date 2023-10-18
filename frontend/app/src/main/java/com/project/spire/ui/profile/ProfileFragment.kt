package com.project.spire.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
//import com.example.spire.databinding.FragmentHomeBinding
import com.example.spire.databinding.FragmentProfileBinding
import com.project.spire.core.auth.AuthPreferenceKeys
import com.project.spire.core.auth.AuthRepository
import com.project.spire.core.auth.authDataStore
import com.project.spire.ui.MainActivity
import com.project.spire.ui.auth.LoginActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val authRepository = AuthRepository(requireContext().authDataStore)
        val viewModelFactory = ProfileViewModelFactory(authRepository)
        val profileViewModel = ViewModelProvider(this, viewModelFactory)[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textProfile
        profileViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // TODO: Remove this after future implementation
        val logoutBtn: Button = binding.tempLogoutButton
        logoutBtn.setOnClickListener {
            val success = profileViewModel.logout()
        }

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

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}