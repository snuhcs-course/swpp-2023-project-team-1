package com.project.spire.ui.create.image

import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.net.Uri
import android.view.MotionEvent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImageCreateViewModel: ViewModel() {
    private val _originImageUri = MutableLiveData<Uri>()
    val originImageUri: LiveData<Uri>
        get() = _originImageUri

    fun setOriginImageUri(uri: Uri) {
        _originImageUri.value = uri
    }

    private val STROKE_PEN = 20f
    private val STROKE_ERASER = 50f
    private val MODE_CLEAR = PorterDuffXfermode(PorterDuff.Mode.CLEAR) // clears when overlapped

    private var _paths = LinkedHashMap<Path, PaintOptions>()
    val paths: LinkedHashMap<Path, PaintOptions>
        get() = _paths
    private var _paintOptions = PaintOptions(STROKE_PEN, null)
    val paintOptions: PaintOptions
        get() = _paintOptions

    private var _currentX = 0f
    private var _currentY = 0f
    private var _startX = 0f
    private var _startY = 0f

    private var _isEraseMode = MutableLiveData<Boolean>(false)
    val isEraseMode: LiveData<Boolean>
        get() = _isEraseMode
    private var _isPenMode = MutableLiveData<Boolean>(true)
    val isPenMode: LiveData<Boolean>
        get() = _isPenMode

    private var _isDrawing = MutableLiveData<Boolean>(false)
    val isDrawing: LiveData<Boolean>
        get() = _isDrawing

    private var _mPath: Path = Path()
    val mPath: Path
        get() = _mPath

    // mPath를 LiveData로 만들고 mPath를 observe하는 것이 맞겠으나, 이 경우 화면 표시에 delay가 발생하여
    // 대신 boolean type의 isDrawing을 observe
    // invalidate()를 호출하여 화면을 다시 그려야 하는 순간마다 isDrawing의 값을 잠시 바꾸는 것으로
    // 화면을 실시간으로 다시 그리도록 구현

    fun clearCanvas() {
        _mPath.reset()
        _paths = LinkedHashMap()
        _isEraseMode.postValue(false)
        _isPenMode.postValue(false)
        _isDrawing.postValue(true)
        _isDrawing.postValue(false)
    }

    fun changeEraseMode() {
        if (!_isEraseMode.value!!) {
            _isEraseMode.postValue(true)
            _isPenMode.postValue(false)
            _paintOptions.strokeWidth = STROKE_ERASER
            _paintOptions.xfermode = MODE_CLEAR
        }
        else {
            _isEraseMode.postValue(false)
        }
    }

    fun changePenMode() {
        if (!_isPenMode.value!!) {
            _isEraseMode.postValue(false)
            _isPenMode.postValue(true)
            _paintOptions.strokeWidth = STROKE_PEN
            _paintOptions.xfermode = null
        }
        else {
            _isPenMode.postValue(false)
        }
    }

    fun processCanvasMotionEvent(event: MotionEvent): Boolean {
        if (!_isPenMode.value!! and !_isEraseMode.value!!) return true;
        // don't need to track path is pen and eraser are both disabled

        val y = event.y
        val x = event.x
        if (event.pointerCount == 1) { // don't track on multi-touch
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    _startX = x
                    _startY = y

                    _mPath.reset()
                    _mPath.moveTo(x, y)
                    _currentX = x
                    _currentY = y

                    _isDrawing.postValue(true)
                    _isDrawing.postValue(false)
                }
                MotionEvent.ACTION_MOVE -> {
                    _mPath.quadTo(_currentX, _currentY, (x + _currentX) / 2, (y + _currentY) / 2)
                    _currentX = x
                    _currentY = y

                    _isDrawing.postValue(true)
                    _isDrawing.postValue(false)
                }
                MotionEvent.ACTION_UP -> {
                    _mPath.lineTo(_currentX, _currentY)

                    // draw a dot on click
                    if (_startX == _currentX && _startY == _currentY) {
                        _mPath.lineTo(_currentX, _currentY + 2)
                        _mPath.lineTo(_currentX + 1, _currentY + 2)
                        _mPath.lineTo(_currentX + 1, _currentY)
                    }
                    _paths[_mPath] = _paintOptions
                    _mPath = Path()
                    _paintOptions = PaintOptions(_paintOptions.strokeWidth, _paintOptions.xfermode)
                    _isDrawing.postValue(true)
                    _isDrawing.postValue(false)
                }
            }
        }
        return true
    }
}