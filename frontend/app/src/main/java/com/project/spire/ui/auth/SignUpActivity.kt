package com.project.spire.ui.auth

import android.content.Intent
import android.os.Bundle

import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spire.R

import com.example.spire.databinding.ActivitySignUpBinding
import com.project.spire.ui.MainActivity


class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
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



        emailInput.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                emailInput.helperText = resources.getString(R.string.email_helper_text)
                emailInput.error = null
                emailInput.isErrorEnabled = false
            } else {
                emailInput.helperText = ""
            }
        }


        passwordInput.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                passwordInput.helperText = resources.getString(R.string.password_helper_text)
                passwordInput.error = null
                passwordInput.isErrorEnabled = false
            } else {
                passwordInput.helperText = ""
            }
        }



        usernameInput.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                usernameInput.helperText = resources.getString(R.string.username_helper_text)
                usernameInput.error = null
                usernameInput.isErrorEnabled = false
            } else {
                usernameInput.helperText = ""
            }
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
                    binding.LoadingIndicator.hide()
                }


            }


        }


    }

}