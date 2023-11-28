package com.project.spire.ui.create

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.spire.R
import com.example.spire.databinding.ActivityWriteTextBinding
import com.project.spire.network.post.response.PostError
import com.project.spire.network.post.response.PostSuccess
import com.project.spire.ui.MainActivity
import com.project.spire.ui.feed.PostActivity
import com.project.spire.utils.InferenceUtils


class WriteTextActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteTextBinding
    private lateinit var carouselAdapter: CarouselAdapter
    private lateinit var inferenceViewModel: InferenceViewModel

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityWriteTextBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val appbar = binding.writeTextAppBarLayout
        appbar.toolbarText.setText(R.string.title_toolbar_write_text)

        inferenceViewModel = InferenceUtils.inferenceViewModel
        inferenceViewModel.reset()

        val carousel = binding.carouselRecyclerView
        val doneButton = binding.doneButton
        val regenerateBtn = binding.regenerateBtn
        val originalImageBtn = binding.originalImageBtn
        val downloadBtn = binding.downloadBtn
        val backBtn = binding.writeTextAppBarLayout.backButton
        val exitBtn = binding.writeTextAppBarLayout.exitButton

        inferenceViewModel.inferenceError.observe(this) {
            if (it == 1) {
                //Toast.makeText(this, "Image edit failed, retrying...", Toast.LENGTH_LONG).show()
            }
            else if (it == 2) {
                Toast.makeText(this, "Image edit failed, please try again.", Toast.LENGTH_LONG).show()
                onBackPressedDispatcher.onBackPressed()
                finish()
            }
        }

        // Inference result received
        inferenceViewModel.inferenceResult.observe(this) {
            if (it != null) {
                Log.d("WriteTextActivity", "Inference result received. Changing image.")

                binding.resultImageView.visibility = View.INVISIBLE
                binding.loadingProgressBar.isActivated = false
                binding.loadingProgressBar.visibility = View.GONE
                binding.loadingText.visibility = View.INVISIBLE
                binding.loadingTimeText.visibility = View.INVISIBLE
                regenerateBtn.visibility = View.VISIBLE
                originalImageBtn.visibility = View.VISIBLE
                downloadBtn.visibility = View.VISIBLE

                carouselAdapter = CarouselAdapter(it)

                carousel.visibility = View.VISIBLE
                carousel.onFlingListener = null
                carousel.adapter = carouselAdapter
                carousel.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

                val snapHelper = PagerSnapHelper()  // Can only snap to one item at a time
                snapHelper.attachToRecyclerView(carousel)

                binding.indicator.attachToRecyclerView(carousel, snapHelper)

                // Button UI change
                if (inferenceViewModel.previousInference.value is Txt2Img) {
                    originalImageBtn.visibility = View.GONE
                }
            }
        }

        // Post upload result received
        inferenceViewModel.postResult.observe(this) {
            if (it is PostSuccess) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            } else if (it is PostError) {
                Toast.makeText(this, "Post upload failed, please try again", Toast.LENGTH_SHORT).show()
                binding.postUploadProgressBar.visibility = View.GONE
            }
        }

        doneButton.setOnClickListener {
            binding.postUploadProgressBar.visibility = View.VISIBLE
            val currentPosition = (carousel.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            val currentImage: Bitmap = inferenceViewModel.inferenceResult.value!![currentPosition]
            val content = binding.postTextInputLayout.editText?.text.toString()
            inferenceViewModel.postUpload(currentImage, content)
        }

        regenerateBtn.setOnClickListener {
            regenerateDialog()
        }

        originalImageBtn.setOnTouchListener { _, event ->
            showOriginalImage(event)
            true
        }

        downloadBtn.setOnClickListener {
            val currentPosition = (carousel.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            val currentImage: Bitmap = inferenceViewModel.inferenceResult.value!![currentPosition]
            InferenceUtils.saveImage(this, currentImage)
        }

        backBtn.setOnClickListener {
            backDialog()
        }

        exitBtn.setOnClickListener {
            exitDialog()
        }
    }

    private fun backDialog() {
        AlertDialog.Builder(this)
            .setTitle("Do you want to go back?")
            .setMessage("Current image will be lost.")
            .setPositiveButton("Yes") { _, _ ->
                onBackPressedDispatcher.onBackPressed()
                finish()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun exitDialog() {
        AlertDialog.Builder(this)
            .setTitle("Do you want to exit?")
            .setMessage("Current image will be lost.")
            .setPositiveButton("Yes") { _, _ ->
                val intent = Intent(this, MainActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun regenerateDialog() {
        AlertDialog.Builder(this)
            .setTitle("Regenerate Image")
            .setMessage("Current image will be lost.")
            .setPositiveButton("Yes") { _, _ ->
                regenerateImage()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun regenerateImage() {
        when (val previousInference = inferenceViewModel.previousInference.value) {
            is Inpainting -> {
                inferenceViewModel.infer(
                    previousInference.image,
                    previousInference.mask,
                    previousInference.prompt
                )
                startActivity(Intent(this, WriteTextActivity::class.java))
                finish()
            }

            is Txt2Img -> {
                inferenceViewModel.infer(previousInference.prompt)
                startActivity(Intent(this, WriteTextActivity::class.java))
                finish()
            }

            else -> {
                Log.e("WriteTextActivity", "Previous inference is null")
                Toast.makeText(this, "Cannot regenerate image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showOriginalImage(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val previousInference = inferenceViewModel.previousInference.value
                if (previousInference is Inpainting) {
                    binding.originalImageView.setImageBitmap(previousInference.image)
                    binding.originalImageCardView.visibility = View.VISIBLE
                }
            }
            MotionEvent.ACTION_UP -> {
                binding.originalImageCardView.visibility = View.INVISIBLE
            }
        }
    }
}

