package com.project.spire.ui.create

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.spire.R
import com.example.spire.databinding.ActivityWriteTextBinding
import com.project.spire.ui.MainActivity
import com.project.spire.utils.InferenceUtils


class WriteTextActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteTextBinding
    private lateinit var carouselAdapter: CarouselAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityWriteTextBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val appbar = binding.writeTextAppBarLayout
        appbar.toolbarText.setText(R.string.title_toolbar_write_text)

        val inferenceViewModel = InferenceUtils.inferenceViewModel
        inferenceViewModel.reset()

        val carousel = binding.carouselRecyclerView
        val doneButton = binding.doneButton

        inferenceViewModel.inferenceResult.observe(this) {
            if (it != null) {
                Log.d("WriteTextActivity", "Inference result received. Changing image.")

                binding.resultImageView.visibility = View.INVISIBLE
                binding.loadingProgressBar.isActivated = false
                binding.loadingProgressBar.visibility = View.GONE
                binding.loadingText.visibility = View.GONE
                binding.loadingTimeText.visibility = View.GONE

                carousel.visibility = View.VISIBLE
                carousel.onFlingListener = null
                carousel.adapter = CarouselAdapter(it)
                carousel.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

                val snapHelper = PagerSnapHelper()  // Can only snap to one item at a time
                snapHelper.attachToRecyclerView(carousel)

                binding.indicator.attachToRecyclerView(carousel, snapHelper)
            }
        }


        doneButton.setOnClickListener {
            // TODO: Send post upload request
        }

        val backBtn = binding.writeTextAppBarLayout.backButton
        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            finish()
        }
        val exitBtn = binding.writeTextAppBarLayout.exitButton
        exitBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            finish()
        }
    }
}

