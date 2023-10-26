package com.project.spire.ui.create.image

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.net.toUri
import com.example.spire.R
import com.example.spire.databinding.ActivityCropImageBinding
import java.io.ByteArrayOutputStream

class CropImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCropImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityCropImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uri = intent.getStringExtra("imageUri")
        val cropImageView = binding.cropImageView
        cropImageView.setImageUriAsync(uri?.toUri())
        cropImageView.setAspectRatio(1, 1)

        val nextButton = binding.nextButton
        nextButton.setOnClickListener {
            val croppedBitmap = cropImageView.getCroppedImage()!!
            val intent = Intent(this, ImageEditActivity::class.java)
            intent.putExtra("imageBitmap", croppedBitmap)
            startActivity(intent)
        }
    }
}