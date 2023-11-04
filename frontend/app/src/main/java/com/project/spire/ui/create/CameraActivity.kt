package com.project.spire.ui.create

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.spire.R
import com.example.spire.databinding.ActivityCameraBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.Executors
import kotlin.math.max

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private lateinit var previewView: PreviewView
    private lateinit var imageCapture: ImageCapture
    private lateinit var safeContext: Context
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var lensFacing = CameraSelector.LENS_FACING_BACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        safeContext = this
        supportActionBar?.hide()
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        previewView = binding.previewView
        val captureButton = binding.captureButton

        handleCameraUI()

        // Request camera permissions
        val cameraPermissionCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (cameraPermissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1000)
        }

        startCamera()

        captureButton.setOnClickListener {
            binding.cameraProgressBar.show()
            onTakePhoto()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.cameraProgressBar.hide()
    }

    // Initialize camera
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(safeContext)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    // Bind camera to preview
    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        val preview = Preview.Builder().build()
        imageCapture = ImageCapture.Builder().build()
        try {
            preview.setSurfaceProvider(previewView.surfaceProvider)  // Get preview output surface
            cameraProvider.unbindAll()  // Unbind the use-cases before rebinding them
            val camera = cameraProvider.bindToLifecycle(
                this as LifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (exc: Exception) {
            Log.e("CameraFragment", "Use case binding failed", exc)
        }
    }

    // Switch camera between front and back
    private fun onSwitchCamera() {
        lensFacing = when (lensFacing) {
            CameraSelector.LENS_FACING_FRONT -> CameraSelector.LENS_FACING_BACK
            CameraSelector.LENS_FACING_BACK -> CameraSelector.LENS_FACING_FRONT
            else -> CameraSelector.LENS_FACING_BACK
        }
        startCamera()
    }

    // Take photo
    private fun onTakePhoto() {
        // Add content values
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, getDateTimeFormat())
        contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)

        // Set output file options
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
            this.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        // Take picture with preset thread
        imageCapture.takePicture(
            outputFileOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                // Success
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val outputURI = outputFileResults.savedUri
                    Log.d("CameraActivity", "Image saved to $outputURI")
                    val intent = Intent(safeContext, ImageEditActivity::class.java)
                    intent.putExtra("imageUri", outputURI.toString())
                    startActivity(intent)
                }
                // Error
                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(safeContext, "Error saving photo", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getDateTimeFormat(): String {
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss")
        return simpleDateFormat.format(Date())
    }

    private fun handleCameraUI() {
        // Handle UI
        val displayDensity = applicationContext.resources.displayMetrics.density
        val displayWidth = applicationContext.resources.displayMetrics.widthPixels
        val displayHeight = applicationContext.resources.displayMetrics.heightPixels
        Log.d("CameraActivity", "Density: $displayDensity Width: $displayWidth Height: $displayHeight")
        val cameraBlindTop = binding.cameraBlindTop
        val cameraBlindBottom = binding.cameraBlindBottom

        cameraBlindTop.post {
            val params = cameraBlindTop.layoutParams as ConstraintLayout.LayoutParams
            params.height = (displayHeight - displayWidth ) / 2 - 100
            Log.d("CameraActivity", "Height: ${params.height}")
            cameraBlindTop.layoutParams = params
            cameraBlindTop.requestLayout()
        }

        cameraBlindBottom.post {
            val params = cameraBlindBottom.layoutParams as ConstraintLayout.LayoutParams
            params.height = displayHeight - cameraBlindTop.layoutParams.height - displayWidth
            Log.d("CameraActivity", "Height: ${params.height}")
            cameraBlindBottom.layoutParams = params
            cameraBlindBottom.requestLayout()
        }

        Log.d("CameraActivity", "Top: ${cameraBlindTop.height} Bottom: ${cameraBlindBottom.height}")
    }

    private fun checkCameraHardware(): Boolean {
        return this.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }
}