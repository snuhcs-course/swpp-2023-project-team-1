package com.project.spire.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.spire.R
import com.example.spire.databinding.ActivityErrorBinding
import com.project.spire.ui.auth.AutoLoginActivity

const val DEBUG = false

class ErrorActivity : AppCompatActivity() {


    private lateinit var binding: ActivityErrorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityErrorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val errorMsg = intent.getStringExtra(ERROR_MSG)
        Log.e("ErrorActivity", "Error msg: $errorMsg")
        /*
        val errorTag = intent.getStringExtra(ERROR_TAG)
        if (errorTag == ERROR_TAG_CONNECTION_LOST) {
            binding.errorText2.setText(R.string.error_text_connection)
        }
        else {
            binding.errorText2.setText(R.string.error_text_2)
            Log.e("ErrorActivity", "Error Tag: $errorTag")
        } */
        binding.errorText2.setText(R.string.error_text_connection)

        if (DEBUG) {
            binding.errorText3.setText(errorMsg)
        }

        binding.exitButton.setOnClickListener {
            connectionLostDialog()
        }
        binding.restartButton.setOnClickListener {
            Log.d("ErrorActivity", "Restarting app")
            val intent = Intent(this, AutoLoginActivity::class.java)
            startActivity(intent)
            Log.d("ErrorActivity", "finishAffinity()")
            finishAffinity()
        }
    }

    private fun connectionLostDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Quit Spire")
            .setMessage("Are you sure to quit?")
            .setPositiveButton("Yes") { _, _ ->
                Log.d("ErrorActivity", "Terminating app")
                finishAffinity()
                finish()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    companion object {
        const val ERROR_TAG = "error_tag"
        const val ERROR_MSG = "error_msg"
        const val ERROR_TAG_CONNECTION_LOST = "connection_lost"
    }
}