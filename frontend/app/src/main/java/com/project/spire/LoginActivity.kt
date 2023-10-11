package com.project.spire

import android.content.Intent
import android.os.Bundle

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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



        val goToSignupTextBtn: TextView = binding.GoToSignupTextBtn

        goToSignupTextBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

    }

}
