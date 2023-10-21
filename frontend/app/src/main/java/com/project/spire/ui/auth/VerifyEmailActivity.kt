package com.project.spire.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.spire.R
import com.example.spire.databinding.ActivitySignUpBinding
import com.example.spire.databinding.ActivityVerifyEmailBinding

class VerifyEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyEmailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide the action bar
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        val goToLoginTextBtn: TextView = binding.goToLoginTextBtn

        goToLoginTextBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}