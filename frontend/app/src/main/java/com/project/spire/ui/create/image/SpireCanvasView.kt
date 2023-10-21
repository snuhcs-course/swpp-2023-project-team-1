package com.project.spire.ui.create.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.spire.R

import java.io.Serializable



data class PaintOptions(var color: Int, var strokeWidth: Float, var xfermode: PorterDuffXfermode?) :
    Serializable

class SpireCanvasView(internal var context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val COLOR_BLUE = ContextCompat.getColor(context, R.color.blue_500)
    private val COLOR_GRAY = ContextCompat.getColor(context, R.color.grey_300)
    private val STROKE_PEN = 20f
    private val STROKE_ERASER = 50f
    private val MODE_CLEAR = PorterDuffXfermode(PorterDuff.Mode.CLEAR) // clears when overlapped
    // drawing canvas

    private var paths = LinkedHashMap<Path, PaintOptions>()
    private var paintOptions = PaintOptions(COLOR_BLUE, STROKE_PEN, null)
    private var currentX = 0f
    private var currentY = 0f
    private var startX = 0f
    private var startY = 0f

    var isEraseMode = false
    var isPenMode = true
    private var mbitmap: Bitmap? = null
    private var mCanvas: Canvas? = null
    private var mPath: Path = Path()
    private var mPaint: Paint = Paint()

    init {
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for ((path, options) in paths) {
            mPaint.color = options.color
            mPaint.xfermode = options.xfermode
            mPaint.strokeWidth = options.strokeWidth
            if (options.xfermode != null) {
                setLayerType(LAYER_TYPE_HARDWARE, null)
                // xfermode doesn't works with hardware acceleration
            }
            canvas.drawPath(path, mPaint)

        }
        mPaint.color = paintOptions.color
        mPaint.xfermode = paintOptions.xfermode
        mPaint.strokeWidth = paintOptions.strokeWidth
        if (isEraseMode) {
            setLayerType(LAYER_TYPE_HARDWARE, null)
        } else {
            mPaint.color = paintOptions.color
        }
        canvas.drawPath(mPath, mPaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw:Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mbitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mbitmap!!)
    }

    fun clearCanvas() {
        mPath.reset()
        paths = LinkedHashMap()
        isEraseMode = false
        isPenMode = false
        invalidate()
    }

    fun eraseMode() {
        isEraseMode = true
        isPenMode = false
        paintOptions.color = COLOR_GRAY
        paintOptions.strokeWidth = STROKE_ERASER
        paintOptions.xfermode = MODE_CLEAR

    }

    fun penMode() {
        isEraseMode = false
        isPenMode = true
        paintOptions.color = COLOR_BLUE
        paintOptions.strokeWidth = STROKE_PEN
        paintOptions.xfermode = null
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isPenMode and !isEraseMode) return true;
        // don't need to track path is pen and eraser are both disabled

        val y = event.y
        val x = event.x
        if (event.pointerCount == 1) { // don't track on multi-touch
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = x
                    startY = y
                    mPath.reset()
                    mPath.moveTo(x, y)
                    currentX = x
                    currentY = y
                    invalidate()
                }
                MotionEvent.ACTION_MOVE -> {
                    mPath.quadTo(currentX, currentY, (x + currentX) / 2, (y + currentY) / 2)
                    currentX = x
                    currentY = y
                    invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    mPath.lineTo(currentX, currentY)

                    // draw a dot on click
                    if (startX == currentX && startY == currentY) {
                        mPath.lineTo(currentX, currentY + 2)
                        mPath.lineTo(currentX + 1, currentY + 2)
                        mPath.lineTo(currentX + 1, currentY)
                    }

                    paths[mPath] = paintOptions
                    mPath = Path()
                    paintOptions = PaintOptions(paintOptions.color, paintOptions.strokeWidth, paintOptions.xfermode)
                    invalidate()
                }
            }
        }
        return true
    }
}