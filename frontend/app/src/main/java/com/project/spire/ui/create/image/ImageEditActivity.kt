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

        editBtn = binding.editButton
        editBtn.setOnClickListener {
            if (mCanvasView.isPenMode){
                mCanvasView.isPenMode = false
                editBtn.setImageResource(R.drawable.ic_img_edit)
            }
            else {
                mCanvasView.penMode()
                editBtn.setImageResource(R.drawable.ic_img_edit_selected)
                eraseBtn.setImageResource(R.drawable.ic_img_erase)
            }
        }

        eraseBtn = binding.eraseButton
        eraseBtn.setOnClickListener {
            if (mCanvasView.isEraseMode){
                mCanvasView.isEraseMode = false
                eraseBtn.setImageResource(R.drawable.ic_img_erase)
            }
            else {
                mCanvasView.eraseMode()
                eraseBtn.setImageResource(R.drawable.ic_img_erase_selected)
                editBtn.setImageResource(R.drawable.ic_img_edit)
            }
        }

        resetBtn = binding.resetButton
        resetBtn.setOnClickListener {
            mCanvasView.clearCanvas()
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