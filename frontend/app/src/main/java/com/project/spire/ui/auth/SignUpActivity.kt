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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.spire.R
import com.example.spire.databinding.ActivitySignUpBinding
import com.google.android.material.textfield.TextInputLayout
import com.project.spire.core.auth.Validation
import com.project.spire.ui.MainActivity


class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        val goToLoginTextBtn: TextView = binding.goToLoginTextBtn

        goToLoginTextBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
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
            val email = emailInput.editText?.text.toString()
            val password = passwordInput.editText?.text.toString()
            val username = usernameInput.editText?.text.toString()

            emailInput.clearFocus()
            passwordInput.clearFocus()
            usernameInput.clearFocus()


            var IsValid = true

            //TODO: Add Validate email, password, and username Logic

            if (email.isEmpty()) {
                IsValid = false

                emailInput.error = "Email is required"

            }
            if (password.isEmpty()) {
                IsValid = false
                passwordInput.error = "Password is required"

            }
            if (username.isEmpty()) {
                IsValid = false
                usernameInput.error = "Username is required"
            }

            if (IsValid) {
                binding.loadingIndicator.show()

                //TimeUnit.MILLISECONDS.sleep(2000)


                var succeed = true
                if (succeed) {

                    binding.loadingIndicator.hide()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to create auth", Toast.LENGTH_SHORT).show()
                    binding.loadingIndicator.hide()
                }
            }
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