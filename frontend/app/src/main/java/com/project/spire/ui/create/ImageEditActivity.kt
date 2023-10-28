package com.project.spire.ui.create

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.spire.R
import com.example.spire.databinding.ActivityImageEditBinding
import com.project.spire.core.inference.InferenceRepository
import com.project.spire.utils.InferenceUtils
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

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

            // FIXME: save image to local?
            // saveImageOnAboveAndroidQ(mCanvasView.getBitmap())
            // if (mImageBitmap != null) {
            // saveImageOnAboveAndroidQ(mImageBitmap!!)
            // }

            inferenceViewModel.infer(mImageBitmap!!, mCanvasView.getBitmap(), promptInput.text.toString())
        }

        // TODO: implement toolbar buttons
    }

    fun saveImageOnAboveAndroidQ(bitmap: Bitmap) {
        val fileName = System.currentTimeMillis().toString() + ".png"
        val contentValues = ContentValues()
        contentValues.apply {
            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/ImageSave")
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val uri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            if (uri != null) {
                val image = contentResolver.openFileDescriptor(uri, "w", null)
                if (image != null) {
                    val fos = FileOutputStream(image.fileDescriptor)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                    fos.close()

                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    contentResolver.update(uri, contentValues, null, null)
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}