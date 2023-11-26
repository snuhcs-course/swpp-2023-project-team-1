package com.project.spire.ui.create

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.spire.R
import com.example.spire.databinding.ActivityImageEditBinding
import com.project.spire.ui.MainActivity
import com.project.spire.utils.BitmapUtils
import com.project.spire.utils.InferenceUtils

class ImageEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageEditBinding
    private lateinit var inferenceViewModel: InferenceViewModel
    private val canvasViewModel: CanvasViewModel by viewModels()
    private var mImageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityImageEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        inferenceViewModel = InferenceUtils.inferenceViewModel

        val uri = Uri.parse(intent.getStringExtra("imageUri"))
        val cropImageView = binding.cropImageView
        val cropDoneButton = binding.cropDoneButton
        cropImageView.setImageUriAsync(uri)
        cropImageView.setAspectRatio(1, 1)
        val mImageView = binding.editingImage

        cropDoneButton.setOnClickListener {
            val bitmap = cropImageView.getCroppedImage(mImageView.width, mImageView.height)!!
            Log.d("ImageEditActivity", "Bitmap size: ${bitmap.height}")
            canvasViewModel.setOriginImageBitmap(bitmap)
            cropImageView.visibility = ImageView.GONE
            cropDoneButton.visibility = Button.GONE
        }

        canvasViewModel.originImageBitmap.observe(this) {
            if (it != null) {
                Log.d("ImageEditActivity", "Image updated")
                mImageBitmap = Bitmap.createScaledBitmap(it, mImageView.width, mImageView.height,false)
                mImageView.setImageBitmap(mImageBitmap)
            }
        }

        val mCanvasView = binding.spireCanvasView
        mCanvasView.initViewModel(canvasViewModel)

        val editBtn = binding.editButton
        editBtn.setOnClickListener { canvasViewModel.changePenMode() }
        val penModeObserver = Observer<Boolean> { isPenMode ->
            if (isPenMode) {
                editBtn.setImageResource(R.drawable.ic_img_edit_selected)
            } else {
                editBtn.setImageResource(R.drawable.ic_img_edit)
            }
        }
        canvasViewModel.isPenMode.observe(this, penModeObserver)

        val eraseBtn = binding.eraseButton
        eraseBtn.setOnClickListener { canvasViewModel.changeEraseMode() }
        val eraseModeObserver = Observer<Boolean> { isEraseMode ->
            if (isEraseMode) {
                eraseBtn.setImageResource(R.drawable.ic_img_erase_selected)
            } else {
                eraseBtn.setImageResource(R.drawable.ic_img_erase)
            }
        }
        canvasViewModel.isEraseMode.observe(this, eraseModeObserver)

        val isDrawingObserver = Observer<Boolean> { mCanvasView.invalidate() }
        canvasViewModel.isDrawing.observe(this, isDrawingObserver)

        val resetBtn = binding.resetButton
        resetBtn.setOnClickListener {
            canvasViewModel.clearCanvas()
            editBtn.setImageResource(R.drawable.ic_img_edit)
            eraseBtn.setImageResource(R.drawable.ic_img_erase)
        }
        val promptInput = binding.promptInput
        /* val promptSuggestBtn = binding.promptSuggestionButton


        promptSuggestBtn.setOnClickListener {
            val currentText = promptInput.text.toString()
            if (currentText == "") promptInput.setText(promptSuggestBtn.text)
            else {
                promptInput.setText(currentText + ", " + promptSuggestBtn.text.toString())
            }
        } */

        val nextBtn = binding.nextButton
        nextBtn.setOnClickListener {
            val intent = Intent(this, WriteTextActivity::class.java)
            startActivity(intent)
            val maskBitmap = mCanvasView.getBitmap()
            val maskBitmapToServer = BitmapUtils.maskTransparentToBlack(maskBitmap)
            inferenceViewModel.infer(mImageBitmap!!, maskBitmapToServer, promptInput.text.toString())
        }

        val backBtn = binding.imageEditAppBarLayout.backButton
        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        val exitBtn = binding.imageEditAppBarLayout.exitButton
        exitBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        val fetchButton = binding.fetchButton
        fetchButton.setOnClickListener {
            inferenceViewModel.inferMask(mImageBitmap!!, mImageView.width, mImageView.height)
            fetchButton.visibility = Button.GONE
        }

        inferenceViewModel.maskOverallImage.observe(this) {
            // TODO fix this
            if (it != null ) {
                Log.d("ImageEditActivity", "Mask image received")
                canvasViewModel.applyFetchedMask(it)
            }
        }

        canvasViewModel.backgroundMaskBitmap.observe(this) {
            if (it != null) {
                Log.d("ImageEditActivity", "Background mask bitmap received")
                mCanvasView.invalidate()
            }
        }

     //   val newMask = BitmapFactory.decodeResource(resources, R.drawable.img_dummy_mask)
     //   canvasViewModel.applyFetchedMask(newMask)

    }
}
