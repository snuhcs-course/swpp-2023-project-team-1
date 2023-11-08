package com.project.spire.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.transform.CircleCropTransformation
import com.example.spire.R
import com.example.spire.databinding.ActivityEditProfileBinding
import com.project.spire.core.auth.AuthRepository
import com.project.spire.core.auth.authDataStore
import com.project.spire.core.user.UserRepository
import com.project.spire.ui.auth.LoginActivity

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val authRepository = AuthRepository(this.authDataStore)
        val userRepository = UserRepository()
        val viewModelFactory = ProfileViewModelFactory(authRepository, userRepository)
        profileViewModel = ViewModelProvider(this, viewModelFactory)[ProfileViewModel::class.java]

        profileViewModel.getMyInfo()

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.editProfileDropdownMenu.setOnClickListener {
            val popupMenu = PopupMenu(this, binding.editProfileDropdownMenu)
            popupMenu.menuInflater.inflate(R.menu.edit_profile_dropdown, popupMenu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener { menuItem ->
                popupMenuItemClickListener(menuItem)
            }
        }

        binding.editProfileSaveBtn.setOnClickListener {
            // TODO: Save the changes to the user's profile

        }
    }

    override fun onStart() {
        super.onStart()
        profileViewModel.email.observe(this) {
            binding.editProfileEmailInput.editText?.setText(it)
        }
        profileViewModel.username.observe(this) {
            binding.editProfileUsernameInput.editText?.setText(it)
        }
        profileViewModel.bio.observe(this) {
            binding.editProfileBioInput.editText?.setText(it)
        }
        profileViewModel.profileImageUrl.observe(this) {
            binding.editProfileImage.load(it) {
                crossfade(true)
                transformations(CircleCropTransformation())
            }
        }
        profileViewModel.logoutSuccess.observe(this) {
            if (it) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun popupMenuItemClickListener(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.edit_profile_logout_btn -> {
                logoutAlertDialog()
                true
            }

            else -> {
                deleteAccountAlertDialog()
                true
            }
        }
    }

    private fun logoutAlertDialog() {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.logout_dialog_text))
            .setPositiveButton("Log Out") { dialog, _ ->
                profileViewModel.logout()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteAccountAlertDialog() {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.delete_account_dialog_text))
            .setPositiveButton("Delete") { dialog, _ ->
                // TODO: Delete the user's account

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}