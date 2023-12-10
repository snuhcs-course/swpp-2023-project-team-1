package com.project.spire.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.spire.R
import com.example.spire.databinding.ActivityVerifyEmailBinding
import com.google.android.material.textfield.TextInputLayout
import com.project.spire.utils.DataStoreProvider
import com.project.spire.core.auth.AuthRepository
import com.project.spire.utils.VerifyEmailViewModelFactory

class VerifyEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val authDataStore = DataStoreProvider.authDataStore
        val authRepository = AuthRepository(authDataStore)
        val viewModelFactory = VerifyEmailViewModelFactory(authRepository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[VerifyEmailViewModel::class.java]

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
        val sendAgainButton = binding.verificationSendAgain
        val sendAgainTimer = binding.verificationSendAgainTimer
        val verifyError = binding.verifyErrorText
        val codeFilled = MutableList(6) { -1 }

        sendMailButton.setOnClickListener {
            viewModel.sendEmail(emailInput.editText?.text.toString())
        }

        sendAgainButton.setOnClickListener {
            viewModel.sendEmail(emailInput.editText?.text.toString())
            viewModel.startTimer()
            sendAgainButton.isEnabled = false
        }

        viewModel.emailSent.observe(this) {
            when (it) {
                true -> {
                    viewModel.startTimer()
                    sendMailButton.visibility = View.GONE
                    verificationLayout.visibility = View.VISIBLE
                    emailInput.isEnabled = false
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

        viewModel.remainingSeconds.observe(this) {
            if (it > 0) {
                sendAgainTimer.text = "in $it seconds"
            }
        }

        viewModel.canSendAgain.observe(this) {
            sendAgainButton.isEnabled = it
            if (it) {
                sendAgainTimer.text = ""
                sendAgainButton.setTextColor(resources.getColor(R.color.blue_700))
            } else {
                sendAgainButton.setTextColor(resources.getColor(R.color.grey_400))
            }
        }

        viewModel.verifyEmailResult.observe(this) {
            if (it) {
                val intent = Intent(this, SignUpActivity::class.java)
                intent.putExtra("email", emailInput.editText?.text.toString())
                startActivity(intent)
                finish()
            }
        }

        viewModel.verifyErrorMessage.observe(this) {
            verifyError.text = it
        }

        viewModel.emailExists.observe(this) {
            if (it) {
                emailInput.error = "Email already exists"
            }
        }

        for (i in 1..6) {
            val codeInput = findViewById<TextInputLayout>(
                resources.getIdentifier(
                    "verification_code_$i",
                    "id",
                    packageName
                )
            )
            codeInput.editText?.doOnTextChanged { text, _, _, _ ->
                handleFocus(i, text.toString())
                if (text.toString().isNotEmpty()) {
                    codeFilled[i - 1] = text.toString().toInt()
                } else {
                    codeFilled[i - 1] = -1
                }
                Log.d("VerifyEmailActivity", "codeFilled: $codeFilled")
                if (codeFilled.all { it != -1 }) {
                    viewModel.verifyCode(
                        emailInput.editText?.text.toString(),
                        codeFilled.joinToString("")
                    )
                }
            }
        }
    }

    private fun handleFocus(i: Int, text: String) {
        if (text.length == 1 && i < 6) {
            val codeInput = findViewById<TextInputLayout>(
                resources.getIdentifier(
                    "verification_code_${i + 1}",
                    "id",
                    packageName
                )
            )
            codeInput.requestFocus()
        } else if (text.isEmpty() && i > 1) {
            val codeInput = findViewById<TextInputLayout>(
                resources.getIdentifier(
                    "verification_code_${i - 1}",
                    "id",
                    packageName
                )
            )
            codeInput.requestFocus()
        }
    }
}