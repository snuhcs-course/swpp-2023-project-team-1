package com.project.spire.ui.create.image

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.spire.R

class SpireCanvasView(internal var context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val COLOR_BLUE = ContextCompat.getColor(context, R.color.blue_500)
    private lateinit var viewModel: ImageCreateViewModel

    fun initViewModel(viewModel: ImageCreateViewModel) {
        this.viewModel = viewModel
        // TODO: viewmodel을 이런 식으로 불러와도 되나..
    }

    private var mPaint: Paint = Paint()

    init {
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.color = COLOR_BLUE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

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
/*
    override fun onSizeChanged(w: Int, h: Int, oldw:Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mbitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mbitmap!!)
    }
*/

    override fun onTouchEvent(event: MotionEvent): Boolean {
        viewModel.processCanvasMotionEvent(event)
        return true
    }
}