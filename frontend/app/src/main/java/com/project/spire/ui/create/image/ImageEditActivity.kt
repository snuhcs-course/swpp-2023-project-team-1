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
import android.widget.FrameLayout

class ImageEditActivity : AppCompatActivity() {

    private lateinit var mImageView: ImageView
    private lateinit var mCanvasView: SpireCanvasView
    private lateinit var eraseBtn: Button
    private lateinit var resetBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_edit)

        mImageView = findViewById(R.id.editingImage)
        mCanvasView = findViewById(R.id.spireCanvasView)
        mCanvasView.bindImage(mImageView)
        eraseBtn = findViewById(R.id.eraseBtn)
        eraseBtn.setOnClickListener {
            if (mCanvasView.isErasing) mCanvasView.penMode()
            else mCanvasView.eraseMode()
        }
        resetBtn = findViewById(R.id.resetBtn)
        resetBtn.setOnClickListener {
            mCanvasView.clearCanvas()
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