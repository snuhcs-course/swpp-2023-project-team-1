package com.project.spire

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher

import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.example.spire.databinding.ActivitySignUpBinding
import java.util.concurrent.TimeUnit


class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        val goToLoginTextBtn: TextView = binding.GoToLoginTextBtn

        goToLoginTextBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        val signUpBtn: Button = binding.SignUpBtn

        val emailInput = binding.EmailInput
        val passwordInput = binding.PasswordInput
        val usernameInput = binding.UsernameInput



        emailInput.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                emailInput.error = null
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        passwordInput.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                passwordInput.error = null
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        usernameInput.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                usernameInput.error = null
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })




        signUpBtn.setOnClickListener {
            val email = emailInput.editText?.text.toString()
            val password = passwordInput.editText?.text.toString()
            val username = usernameInput.editText?.text.toString()


            emailInput.error=null
            passwordInput.error=null
            usernameInput.error=null

            emailInput.clearFocus()
            passwordInput.clearFocus()
            usernameInput.clearFocus()
            var IsValid = true

            //TODO: Add Validate email, password, and username Logic

            if (email.isEmpty()) {
                IsValid= false
                emailInput.error = "Email is required"
                emailInput.requestFocus()
            }
            if (password.isEmpty()) {
                IsValid= false
                passwordInput.error = "Password is required"
                passwordInput.requestFocus()
            }
            if (username.isEmpty()) {
                IsValid= false
                usernameInput.error = "Username is required"
                usernameInput.requestFocus()
            }

            if (IsValid) {
                binding.LoadingIndicator.show()

                //TimeUnit.MILLISECONDS.sleep(2000)





                var succeed = true
                if (succeed)
                {

                    binding.LoadingIndicator.hide()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to create user", Toast.LENGTH_SHORT).show()
                    binding.LoadingIndicator.hide()
                }




            }






        }


    }

}