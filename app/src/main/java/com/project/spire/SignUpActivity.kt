package com.project.spire

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.spire.R
import com.example.spire.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loginTextBtn: TextView = binding.LoginTextBtn

        loginTextBtn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }


    }
}