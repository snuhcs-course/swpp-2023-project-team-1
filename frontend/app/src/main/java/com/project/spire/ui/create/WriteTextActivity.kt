package com.project.spire.ui.create

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.spire.R
import com.example.spire.databinding.ActivityWriteTextBinding
import com.project.spire.core.inference.InferenceRepository
import com.project.spire.utils.InferenceUtils

class WriteTextActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteTextBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityWriteTextBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val appbar = binding.writeTextAppBarLayout
        appbar.toolbarText.setText(R.string.title_toolbar_write_text)

        val inferenceViewModel = InferenceUtils.inferenceViewModel

        inferenceViewModel.inferenceResult.observe(this) {
            if (it != null) {
                Log.d("WriteTextActivity", "Inference result received. Changing image.")
                binding.resultImageView.setImageBitmap(it)
                binding.loadingProgressBar.isActivated = false
                binding.loadingProgressBar.visibility = android.view.View.GONE
                binding.loadingText.visibility = android.view.View.GONE
                binding.loadingTimeText.visibility = android.view.View.GONE
            }
        }
    }
}