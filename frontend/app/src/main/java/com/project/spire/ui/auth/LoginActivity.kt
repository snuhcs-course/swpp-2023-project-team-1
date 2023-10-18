package com.project.spire.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import com.example.spire.R
import com.example.spire.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputLayout
import com.project.spire.core.auth.AuthPreferenceKeys
import com.project.spire.core.auth.AuthRepository
import com.project.spire.core.auth.authDataStore
import com.project.spire.network.ErrorUtils
import com.project.spire.network.auth.response.LoginError
import com.project.spire.network.auth.response.LoginResponse
import com.project.spire.network.auth.response.LoginSuccess
import com.project.spire.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        authRepository = AuthRepository(this.authDataStore)
        setContentView(binding.root)

        // Hide the action bar
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        // Redirection to signup page
        val goToSignupTextBtn: TextView = binding.GoToSignupTextBtn
        goToSignupTextBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        val loginBtn: Button = binding.LoginBtn
        val emailInput = binding.EmailInput
        val passwordInput = binding.PasswordInput

        emailInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            setHelperText(emailInput, hasFocus, resources.getString(R.string.email_helper_text))
        }

        passwordInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            setHelperText(passwordInput, hasFocus, resources.getString(R.string.password_helper_text))
        }

        loginBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                login(emailInput, passwordInput)
            }
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

    // TODO: separate login logic from UI
    // Should be in ViewModel
    private suspend fun login(emailInput: TextInputLayout, passwordInput: TextInputLayout) {
        val email = emailInput.editText?.text.toString()
        val password = passwordInput.editText?.text.toString()
        var loginResult: LoginResponse? = null

        emailInput.clearFocus()
        passwordInput.clearFocus()

        if (email.isEmpty()) {
            emailInput.error = "Email is required"
        }
        else if (password.isEmpty()) {
            passwordInput.error = "Password is required"
        }
        else {
            CoroutineScope(Dispatchers.Default).async {
                loginResult = authRepository.login(email, password)
            }.await()

            // Login success: redirect to main activity
            if (loginResult is LoginSuccess) {
                binding.LoadingIndicator.hide()
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

            // Login failed
            else {
                when (val message = (loginResult as LoginError).message) {
                    ErrorUtils.WRONG_PASSWORD -> { passwordInput.error = "Wrong Password" }
                    ErrorUtils.USER_NOT_FOUND -> { emailInput.error = "Email Not Found" }
                    else -> { Log.e("LoginActivity", "Error while logging in. Could not request API.\nMessage: $message") }
                }
            }
        }
    }
}
