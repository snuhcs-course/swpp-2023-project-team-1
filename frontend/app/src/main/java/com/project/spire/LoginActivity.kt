package com.project.spire

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spire.R
import com.example.spire.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }


        val goToSignupTextBtn: TextView = binding.goToSignUpTextBtn

        goToSignupTextBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        val loginBtn: Button = binding.loginBtn

        val emailInput = binding.emailInput
        val passwordInput = binding.passwordInput



        emailInput.editText?.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                emailInput.helperText = resources.getString(R.string.email_helper_text)
                emailInput.error = null
                emailInput.isErrorEnabled = false
            } else {
                emailInput.helperText = ""
            }
        })

        passwordInput.editText?.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                passwordInput.helperText = resources.getString(R.string.password_helper_text)
                passwordInput.error = null
                passwordInput.isErrorEnabled = false

            } else {
                passwordInput.helperText = ""
            }
        })





        loginBtn.setOnClickListener {


            val email = emailInput.editText?.text.toString()
            val password = passwordInput.editText?.text.toString()


            emailInput.clearFocus()
            passwordInput.clearFocus()
            var IsValid = true

            //TODO: Add login logic here

            if (email.isEmpty()) {
                IsValid = false
                emailInput.error = "Email is required"


            }
            if (password.isEmpty()) {
                IsValid = false
                passwordInput.error = "Password is required"


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
                    Toast.makeText(this, "Failed to create user", Toast.LENGTH_SHORT).show()
                    binding.loadingIndicator.hide()
                }


            }


        }


    }

    override fun onStart() {
        super.onStart()

        val logined = false
        if (logined) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()

        }


    }


}
