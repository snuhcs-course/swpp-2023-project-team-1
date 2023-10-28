package com.project.spire.ui.create

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.spire.R
import com.example.spire.databinding.ActivityImageEditBinding
import com.project.spire.utils.BitmapUtils
import com.project.spire.utils.InferenceUtils

class ImageEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageEditBinding
    private lateinit var inferenceViewModel: InferenceViewModel
    private val canvasViewModel: CanvasViewModel by viewModels()

    private lateinit var mImageView: ImageView
    private lateinit var mCanvasView: SpireCanvasView
    private lateinit var editBtn: ImageButton
    private lateinit var eraseBtn: ImageButton
    private lateinit var resetBtn: ImageButton
    private lateinit var promptSuggestBtn: Button
    private lateinit var promptInput: EditText
    private lateinit var nextBtn: Button

    private var mImageBitmap: Bitmap? = null


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
        mImageView = binding.editingImage

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
                //mImageView.setImageBitmap(it)
                mImageBitmap = Bitmap.createScaledBitmap(it, mImageView.width, mImageView.height,false)
                mImageView.setImageBitmap(mImageBitmap)
            }
        }

        mCanvasView = binding.spireCanvasView
        mCanvasView.initViewModel(canvasViewModel)

        editBtn = binding.editButton
        editBtn.setOnClickListener { canvasViewModel.changePenMode() }
        val penModeObserver = Observer<Boolean> { isPenMode ->
            if (isPenMode) {
                editBtn.setImageResource(R.drawable.ic_img_edit_selected)
            } else {
                editBtn.setImageResource(R.drawable.ic_img_edit)
            }
        }
        canvasViewModel.isPenMode.observe(this, penModeObserver)

        eraseBtn = binding.eraseButton
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

        resetBtn = binding.resetButton
        resetBtn.setOnClickListener {
            canvasViewModel.clearCanvas()
            editBtn.setImageResource(R.drawable.ic_img_edit)
            eraseBtn.setImageResource(R.drawable.ic_img_erase)
        }

        promptSuggestBtn = binding.promptSuggestionButton
        promptInput = binding.promptInput
        promptSuggestBtn.setOnClickListener {
            val currentText = promptInput.text.toString()
            if (currentText == "") promptInput.setText(promptSuggestBtn.text)
            else {
                promptInput.setText(currentText + ", " + promptSuggestBtn.text.toString())
            }
        }

        nextBtn = binding.nextButton
        nextBtn.setOnClickListener {
            val intent = Intent(this, WriteTextActivity::class.java)
            startActivity(intent)
            val maskBitmap = mCanvasView.getBitmap()
            val maskBitmapToServer = BitmapUtils.changeMaskColor(maskBitmap)

            // FIXME: save image to local?
            /*
            if (mImageBitmap != null) {
                BitmapUtils.saveImageOnAboveAndroidQ(maskBitmap, this.contentResolver)
                BitmapUtils.saveImageOnAboveAndroidQ(maskBitmapToServer, this.contentResolver)
            } */

            inferenceViewModel.infer(mImageBitmap!!, maskBitmapToServer, promptInput.text.toString())
        }

        // TODO: implement toolbar buttons
    }
}