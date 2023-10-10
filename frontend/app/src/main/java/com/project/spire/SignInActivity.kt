package com.project.spire

import android.content.Intent
import android.os.Bundle

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.spire.databinding.ActivitySignInBinding


class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }



        val signUpButton: TextView = binding.SignupTextBtn

        signUpButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

    }

}
