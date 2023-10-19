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

class ImageEditActivity : AppCompatActivity() {

    private lateinit var mImageView: ImageView
    private lateinit var mCanvasView: SpireCanvasView
    private lateinit var eraseBtn: ImageButton
    private lateinit var resetBtn: ImageButton
    private lateinit var promptSuggestBtn: Button
    private lateinit var promptInput: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_edit)
        mImageView = findViewById(R.id.editing_image)
        mCanvasView = findViewById(R.id.spire_canvas_view)
        mCanvasView.bindImage(mImageView)
        eraseBtn = findViewById(R.id.erase_button)
        eraseBtn.setOnClickListener {
            if (mCanvasView.isErasing) mCanvasView.penMode()
            else mCanvasView.eraseMode()
        }
        // TODO: change icon
        resetBtn = findViewById(R.id.reset_button)
        resetBtn.setOnClickListener {
            mCanvasView.clearCanvas()
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
/*
    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d("spireGesture", "activity-level touch detected")
        mScaleDetector.onTouchEvent(event)
        return true
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            Log.d("spireGesture", "activity-level gesture detected")
            mScaleFactor *= detector.scaleFactor

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f))


            //mImageView.scaleX = mScaleFactor
            //mImageView.scaleY = mScaleFactor // TODO: apply to the viewgroup
            //mImageView.invalidate()
            //mCanvasView.scaleX = mScaleFactor
            //mCanvasView.scaleY = mScaleFactor

            //mFrameLayout.scaleX = mScaleFactor
            //mFrameLayout.scaleY = mScaleFactor


            return true
        }
    }*/
}