package com.project.spire.ui.create.image

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.spire.R
import android.content.Intent
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import com.google.android.material.appbar.AppBarLayout

class ImageEditActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_image_edit)

        mImageView = findViewById(R.id.editing_image)
        mCanvasView = findViewById(R.id.spire_canvas_view)

        editBtn = findViewById(R.id.edit_button)
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

        eraseBtn = findViewById(R.id.erase_button)
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

        resetBtn = findViewById(R.id.reset_button)
        resetBtn.setOnClickListener {
            mCanvasView.clearCanvas()
            editBtn.setImageResource(R.drawable.ic_img_edit)
            eraseBtn.setImageResource(R.drawable.ic_img_erase)
        }

        promptSuggestBtn = findViewById(R.id.prompt_suggestion_button)
        promptInput = findViewById(R.id.prompt_input)
        promptSuggestBtn.setOnClickListener {
            val currentText = promptInput.text.toString()
            if (currentText == "") promptInput.setText(promptSuggestBtn.text)
            else {
                promptInput.setText(currentText + ", " + promptSuggestBtn.text.toString())
            }
        }

    }
}