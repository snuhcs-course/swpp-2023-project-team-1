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
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.ImageView

import java.io.Serializable

data class PaintOptions(var color: Int = Color.BLUE, var strokeWidth: Float = 20f, var alpha: Int = 50, var xfermode: PorterDuffXfermode? = null) :
    Serializable

class SpireCanvasView(internal var context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var paths = LinkedHashMap<Path, PaintOptions>()
    private var paintOptions = PaintOptions()
    private var currentX = 0f
    private var currentY = 0f
    private var startX = 0f
    private var startY = 0f
    // pinch zoom
    private var mScaleFactor = 1f
    private lateinit var mImageView: ImageView
    var isErasing = false

    private var previousScaleFactor = 1f
    private var scaleCenterX = 0f
    private var scaleCenterY = 0f
    private var canvasTranslationX = 0f
    private var canvasTranslationY = 0f

    // drawing canvas
    private var mbitmap: Bitmap? = null
    private var mCanvas: Canvas? = null
    private var mPath: Path = Path()
    private var mPaint: Paint = Paint()
    private var mX: Float = 0.toFloat()
    private var mY: Float = 0.toFloat()
/*
    private var scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= detector.scaleFactor
            // Set min and max zoom levels.
            //mScaleFactor = mScaleFactor.coerceAtLeast(0.2f).coerceAtMost(3.0f)
            val deltaScale = mScaleFactor / previousScaleFactor
            canvasTranslationX += (1 - deltaScale) * scaleCenterX
            canvasTranslationY += (1 - deltaScale) * scaleCenterY

            // Update pinch center
            scaleCenterX = detector.focusX
            scaleCenterY = detector.focusY

            previousScaleFactor = mScaleFactor
            invalidate()
            return true
        }
    }
    private val mScaleDetector = ScaleGestureDetector(context, scaleListener)
*/
    init {
        mPaint.isAntiAlias = true
        //mPaint.color = Color.rgb(0, 0, 0)
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeWidth = 20f
    }

    override fun onDraw(canvas: Canvas) {
        Log.d("ERASE: ", isErasing.toString())
        super.onDraw(canvas)
        // Apply translation based on pinch zoom
        /*
        canvas.translate(canvasTranslationX, canvasTranslationY)

        // Apply scaling to the canvas
        canvas.scale(mScaleFactor, mScaleFactor, scaleCenterX, scaleCenterY)
        mImageView.translationX = canvasTranslationX
        mImageView.translationY = canvasTranslationY
        mImageView.scaleX = mScaleFactor
        mImageView.scaleY = mScaleFactor
*/
        for ((path, options) in paths) {
            mPaint.color = options.color
            mPaint.xfermode = options.xfermode
            mPaint.strokeWidth = options.strokeWidth
            if (options.xfermode != null) {
                //mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                //paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
                //mPaint.color = Color.RED
                setLayerType(LAYER_TYPE_HARDWARE, null)
                //mPaint.strokeWidth = 50f
            }
            canvas.drawPath(path, mPaint)

        }
        mPaint.color = paintOptions.color
        mPaint.xfermode = paintOptions.xfermode
        mPaint.strokeWidth = paintOptions.strokeWidth
        if (isErasing) {
            setLayerType(LAYER_TYPE_HARDWARE, null)
            //
            //paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
            //mPaint.color = Color.RED
            //mPaint.strokeWidth = 50f
        } else {
            //mPaint.strokeWidth = 20f
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
        penMode()
        invalidate()
    }

    fun bindImage(imageView: ImageView) {
        mImageView = imageView
    }
    fun eraseMode() {
        isErasing = true
        paintOptions.color = Color.RED
        paintOptions.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        paintOptions.strokeWidth = 50f
    }

    fun penMode() {
        isErasing = false
        paintOptions.color = Color.BLUE
        paintOptions.xfermode = null
        paintOptions.strokeWidth = 20f
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d("spireGesture", "canvasView-level gesture detected")

       //mScaleDetector.onTouchEvent(event)
        val x = event.x // about drawing
        val y = event.y
        if (event.pointerCount == 1) {
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
                    paintOptions = PaintOptions(paintOptions.color, paintOptions.strokeWidth, paintOptions.alpha, paintOptions.xfermode)
                    invalidate()
                }
            }
        }
        return true
    }
}