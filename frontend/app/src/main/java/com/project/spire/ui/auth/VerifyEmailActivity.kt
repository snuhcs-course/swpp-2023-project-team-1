package com.project.spire.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.spire.R
import com.example.spire.databinding.ActivitySignUpBinding
import com.example.spire.databinding.ActivityVerifyEmailBinding
import com.project.spire.core.DataStoreProvider
import com.project.spire.core.auth.AuthRepository

class VerifyEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyEmailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val authDataStore = DataStoreProvider.authDataStore
        val authRepository = AuthRepository(authDataStore)
        val viewModel = ViewModelProvider(this, VerifyEmailViewModelFactory(authRepository))[VerifyEmailViewModel::class.java]

        // Hide the action bar
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        // Go to login page
        val goToLoginTextBtn: TextView = binding.goToLoginTextBtn
        goToLoginTextBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        val emailInput = binding.emailInput
        val sendMailButton = binding.sendMailButton
        val verificationLayout = binding.verificationCodeLayout

        sendMailButton.setOnClickListener {
            viewModel.sendEmail(emailInput.editText?.text.toString())
        }

        viewModel.emailSent.observe(this) {
            when (it) {
                true -> {
                    sendMailButton.visibility = View.GONE
                    verificationLayout.visibility = View.VISIBLE
                }
                false -> {
                    sendMailButton.visibility = View.VISIBLE
                    verificationLayout.visibility = View.GONE
                }
            }
        }

        viewModel.errorMessage.observe(this) {
            emailInput.error = it
        }
    }
}