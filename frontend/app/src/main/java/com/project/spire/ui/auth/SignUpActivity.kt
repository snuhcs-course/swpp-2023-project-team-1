package com.project.spire.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.spire.R
import com.example.spire.databinding.ActivitySignUpBinding
import com.google.android.material.textfield.TextInputLayout
import com.project.spire.utils.DataStoreProvider
import com.project.spire.core.auth.AuthRepository
import com.project.spire.core.auth.Validation
import com.project.spire.ui.MainActivity


class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    @SuppressLint("ClickableViewAccessibility", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val authDataStore = DataStoreProvider.authDataStore
        val authRepository = AuthRepository(authDataStore)
        val viewModel = ViewModelProvider(this, SignUpViewModelFactory(authRepository))[SignUpViewModel::class.java]

        intent.extras?.let {
            val email = it.getString("email")
            binding.emailInput.editText?.setText(email)
            viewModel.updateEmail(email!!)
        }

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        val signUpBtn: Button = binding.signUpBtn
        val emailInput = binding.emailInput
        val passwordInput = binding.passwordInput
        val usernameInput = binding.usernameInput
        val passwordPatternButton = binding.passwordPattern
        val popupView = LayoutInflater.from(this).inflate(R.layout.password_pattern_popup, null)
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        passwordPatternButton.isActivated = false

        emailInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            setHelperText(emailInput, hasFocus, resources.getString(R.string.email_helper_text))
        }
        usernameInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            setHelperText(usernameInput, hasFocus, resources.getString(R.string.username_helper_text))
        }

        passwordInput.editText?.doOnTextChanged { text, _, _, _ ->
            passwordPatternButton.isActivated = true
            if (Validation.isValidPassword(text.toString()) == Validation.PASSWORD_EMPTY ||
                Validation.isValidPassword(text.toString()) == Validation.PASSWORD_INVALID) {
                passwordPatternButton.setImageResource(R.drawable.vector_error_circle_outline)
            } else {
                passwordPatternButton.setImageResource(R.drawable.vector_check_circle_outline)
            }
            checkInputValidity(text.toString(), usernameInput.editText?.text.toString())
        }

        usernameInput.editText?.doOnTextChanged { text, _, _, _ ->
            checkInputValidity(passwordInput.editText?.text.toString(), text.toString())
        }

        passwordPatternButton.setOnTouchListener {v, event ->
            val password = passwordInput.editText?.text.toString()
            val isInvalid = Validation.isValidPassword(password) == Validation.PASSWORD_EMPTY ||
                    Validation.isValidPassword(password) == Validation.PASSWORD_INVALID
            if (isInvalid && passwordPatternButton.isActivated) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        popupWindow.showAsDropDown(v, 0, 0, Gravity.END);
                        v.performClick()
                    }
                    MotionEvent.ACTION_UP -> {
                        popupWindow.dismiss();
                        v.performClick()
                    }
                    else -> false
                }
            } else { false }
        }

        signUpBtn.setOnClickListener {
            passwordInput.clearFocus()
            usernameInput.clearFocus()
            binding.loadingIndicator.show()
            val password = passwordInput.editText?.text.toString()
            val username = usernameInput.editText?.text.toString()
            viewModel.register(password, username)
        }

        viewModel.registerResult.observe(this) {
            binding.loadingIndicator.hide()
            if (it) {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }

        viewModel.errorMessage.observe(this) {
            binding.loadingIndicator.hide()
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkInputValidity(password: String, username: String) {
        val passwordValid = Validation.isValidPassword(password) == Validation.PASSWORD_VALID
        val usernameValid = Validation.isValidUsername(username) == Validation.USERNAME_VALID
        if (passwordValid && usernameValid) {
            binding.signUpBtn.background = resources.getDrawable(R.drawable.btn_bg, null)
            binding.signUpBtn.isEnabled = true
        } else {
            binding.signUpBtn.background = resources.getDrawable(R.drawable.btn_bg_disabled, null)
            binding.signUpBtn.isEnabled = false
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
}