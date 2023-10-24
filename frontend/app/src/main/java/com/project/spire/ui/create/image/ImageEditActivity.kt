package com.project.spire.ui.create.image

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.spire.R
import android.net.Uri
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.example.spire.databinding.ActivityImageEditBinding
import androidx.activity.viewModels
import androidx.lifecycle.Observer

class ImageEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageEditBinding
    private val viewModel: ImageCreateViewModel by viewModels()

    private lateinit var mImageView: ImageView
    private lateinit var mCanvasView: SpireCanvasView
    private lateinit var editBtn: ImageButton
    private lateinit var eraseBtn: ImageButton
    private lateinit var resetBtn: ImageButton
    private lateinit var promptSuggestBtn: Button
    private lateinit var promptInput: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityImageEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uri = Uri.parse(intent.getStringExtra("imageUri"))
        viewModel.setOriginImageUri(uri)
        mImageView = binding.editingImage
        viewModel.originImageUri.observe(this) {
            if (it != null) {
                mImageView.setImageURI(it)
            }
        }

        mCanvasView = binding.spireCanvasView
        mCanvasView.initViewModel(viewModel)

        editBtn = binding.editButton
        editBtn.setOnClickListener { viewModel.changePenMode() }
        val penModeObserver = Observer<Boolean> { isPenMode ->
            if (isPenMode) {
                editBtn.setImageResource(R.drawable.ic_img_edit_selected)
            }
            else {
                editBtn.setImageResource(R.drawable.ic_img_edit)
            }
        }
        viewModel.isPenMode.observe(this, penModeObserver)

        eraseBtn = binding.eraseButton
        eraseBtn.setOnClickListener { viewModel.changeEraseMode() }
        val eraseModeObserver = Observer<Boolean> { isEraseMode ->
            if (isEraseMode) {
                eraseBtn.setImageResource(R.drawable.ic_img_erase_selected)
            }
            else {
                eraseBtn.setImageResource(R.drawable.ic_img_erase)
            }
        }
        viewModel.isEraseMode.observe(this, eraseModeObserver)

        val isDrawingObserver = Observer<Boolean> { mCanvasView.invalidate() }
        viewModel.isDrawing.observe(this, isDrawingObserver)

        resetBtn = binding.resetButton
        resetBtn.setOnClickListener {
            viewModel.clearCanvas()
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

    }
}