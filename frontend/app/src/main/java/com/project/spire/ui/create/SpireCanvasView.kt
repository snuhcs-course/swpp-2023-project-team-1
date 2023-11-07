package com.project.spire.ui.create

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.spire.R


class SpireCanvasView(internal var context: Context, attrs: AttributeSet?) : View(context, attrs) {

    val COLOR_BLUE = ContextCompat.getColor(context, R.color.blue_mask)

    private lateinit var viewModel: CanvasViewModel
    fun initViewModel(viewModel: CanvasViewModel) {
        this.viewModel = viewModel
    }

    private var mPaint: Paint = Paint()

    init {
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.BEVEL
        mPaint.color = COLOR_BLUE
        mPaint.strokeCap = Paint.Cap.ROUND
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint.xfermode = null
        canvas.drawColor(Color.TRANSPARENT)
        if (viewModel.backgroundMaskBitmap.value != null) {
            canvas.drawBitmap(viewModel.backgroundMaskBitmap.value!!, x, y, null) // draw auto-mask from inference server
        }

        for ((path, options) in viewModel.paths) {
            mPaint.xfermode = options.xfermode
            mPaint.strokeWidth = options.strokeWidth
            if (options.xfermode != null) {
                setLayerType(LAYER_TYPE_HARDWARE, null)
                // xfermode doesn't works with hardware acceleration
            }
            canvas.drawPath(path, mPaint)
        }
        mPaint.xfermode = viewModel.paintOptions.xfermode
        mPaint.strokeWidth = viewModel.paintOptions.strokeWidth
        if (viewModel.isEraseMode.value!!) {
            setLayerType(LAYER_TYPE_HARDWARE, null)
        }
       canvas.drawPath(viewModel.mPath, mPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        viewModel.processCanvasMotionEvent(event)
        return true
    }

    fun getBitmap(width: Int = 0, height: Int = 0): Bitmap { // used when save
        lateinit var bitmap: Bitmap

        if ((width == 0) or (height == 0)) {
            bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        }
        else {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        }

        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)
        this.draw(canvas)
        return bitmap
    }
}