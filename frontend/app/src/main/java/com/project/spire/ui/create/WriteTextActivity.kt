package com.project.spire.ui.create

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
        val carousel = binding.carouselRecyclerView

        val doneButton = binding.doneButton

        inferenceViewModel.inferenceResult.observe(this) {
            if (it != null) {
                Log.d("WriteTextActivity", "Inference result received. Changing image.")
                // binding.resultImageView.setImageBitmap(it)
                binding.resultImageView.visibility = android.view.View.GONE
                binding.loadingProgressBar.isActivated = false
                binding.loadingProgressBar.visibility = android.view.View.GONE
                binding.loadingText.visibility = android.view.View.GONE
                binding.loadingTimeText.visibility = android.view.View.GONE

                carouselAdapter = CarouselAdapter(inferenceViewModel.inferenceResult.value!!)
                carousel.adapter = carouselAdapter

                carousel.visibility = android.view.View.VISIBLE

                // val list = listOf(BitmapFactory.decodeResource(resources, R.drawable.logo_empty), BitmapFactory.decodeResource(resources, R.drawable.logo_black), BitmapFactory.decodeResource(resources, R.drawable.img_dummy)) // dummy

                carouselAdapter = CarouselAdapter(it) //CarouselAdapter(list)

                carousel.adapter = carouselAdapter

                // val layoutManager = CarouselLayoutManager()
                // layoutManager.setCarouselStrategy(FullscreenCarouselStrategy())
                // carousel.layoutManager =


            }
        }

      doneButton.setOnClickListener {
          // TODO: Send post upload request
      }

        val backBtn = binding.writeTextAppBarLayout.backButton
        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        val exitBtn = binding.writeTextAppBarLayout.exitButton
        exitBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            finish()
        }
    }
}