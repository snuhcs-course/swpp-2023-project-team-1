package com.project.spire.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.spire.R
import com.example.spire.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputLayout
import com.project.spire.core.auth.AuthRepository
import com.project.spire.core.auth.authDataStore
import com.project.spire.network.ErrorUtils
import com.project.spire.network.auth.response.LoginError
import com.project.spire.network.auth.response.LoginResponse
import com.project.spire.network.auth.response.LoginSuccess
import com.project.spire.ui.MainActivity
import java.lang.NullPointerException


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val authRepository = AuthRepository(authDataStore)
        val viewModelFactory = LoginViewModelFactory(authRepository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]
        setContentView(binding.root)

        // Hide the action bar
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        // Redirection to signup page
        val goToSignupTextBtn: TextView = binding.goToSignUpTextBtn
        goToSignupTextBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        val loginBtn: Button = binding.loginBtn
        val emailInput = binding.emailInput
        val passwordInput = binding.passwordInput

        emailInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            setHelperText(emailInput, hasFocus, resources.getString(R.string.email_helper_text))
        }

        passwordInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            setHelperText(passwordInput, hasFocus, resources.getString(R.string.password_helper_text))
        }

        loginBtn.setOnClickListener {
            viewModel.login(emailInput, passwordInput)
        }

        viewModel.loginResult.observe(this) { loginResult ->
            handleLoginResult(loginResult, emailInput, passwordInput)
        }
    }

    override fun onStart() {
        super.onStart()
        val loggedIn = false // TODO: Auto login
        if (loggedIn) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun setHelperText(view: TextInputLayout, hasFocus: Boolean, text: String) {
        if (hasFocus) {
            view.helperText = text
            view.error = null
            view.isErrorEnabled = false
        } else {
            view.helperText = ""
        }
    }

    private fun handleLoginResult( loginResult: LoginResponse?,
                                   emailInput: TextInputLayout,
                                   passwordInput: TextInputLayout) {
        // Login success: redirect to main activity
        if (loginResult is LoginSuccess) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        // Login failed: show error message
        else {
            try {
                when (val message = (loginResult as LoginError).message) {
                    ErrorUtils.WRONG_PASSWORD -> { passwordInput.error = "Wrong Password" }
                    ErrorUtils.USER_NOT_FOUND -> { emailInput.error = "Email Not Found" }
                    else -> { Log.e("LoginActivity", "Error while logging in. Could not request API.\nMessage: $message") }
                }
            } catch (e: NullPointerException) {
                Log.e("LoginActivity", "Error while logging in. LoginResult is null.")
            }

        }
    }
}
