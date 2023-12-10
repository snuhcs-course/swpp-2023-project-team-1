package com.project.spire.ui.create

import android.graphics.Bitmap
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.Log
import android.view.MotionEvent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.spire.core.inference.SegmentationRepository
import com.project.spire.network.inference.InferenceResponse
import com.project.spire.network.inference.InferenceSuccess
import com.project.spire.utils.BitmapUtils
import com.project.spire.utils.InferenceUtils
import com.project.spire.utils.PaintOptions
import kotlinx.coroutines.launch

class CanvasViewModel(
    private val segmentationRepository: SegmentationRepository
): ViewModel() {
    private val STROKE_PEN = 60f
    private val STROKE_ERASER = 80f
    private val MODE_CLEAR = PorterDuffXfermode(PorterDuff.Mode.CLEAR) // clears when overlapped
    private var _currentX = 0f
    private var _currentY = 0f
    private var _startX = 0f
    private var _startY = 0f

    private var _maskOverallImage = MutableLiveData<Bitmap?>().apply { value = null }
    private var _masks = MutableLiveData<List<Bitmap>>().apply { value = emptyList() }
    private var _labels = MutableLiveData<List<String>>().apply { value = emptyList() }
    private var _maskError = MutableLiveData<Boolean>().apply { value = false }
    private var _originImageBitmap = MutableLiveData<Bitmap>()
    private var _paths = LinkedHashMap<Any, PaintOptions>() // drawing until now, ORDER MATTERS
    // Path for user touch input, Bitmap for fetched mask
    private var _paintOptions = PaintOptions(STROKE_PEN, null)
    private var _isEraseMode = MutableLiveData<Boolean>(false)
    private var _isPenMode = MutableLiveData<Boolean>(true)
    private var _mPath: Path = Path() // is significant only when the user is still touching
    private var _redraw = MutableLiveData<Boolean>(false)

    val maskOverallImage: LiveData<Bitmap?> get() = _maskOverallImage
    val masks: LiveData<List<Bitmap>> get() = _masks
    val labels: LiveData<List<String>> get() = _labels
    val maskError: LiveData<Boolean> get() = _maskError
    val originImageBitmap: LiveData<Bitmap> get() = _originImageBitmap
    val paths: LinkedHashMap<Any, PaintOptions> get() = _paths
    val paintOptions: PaintOptions get() = _paintOptions
    val isEraseMode: LiveData<Boolean> get() = _isEraseMode
    val isPenMode: LiveData<Boolean> get() = _isPenMode
    val mPath: Path get() = _mPath
    val redraw: LiveData<Boolean> get() = _redraw  // change this when need to redraw
    // observe this instead of mPath, to avoid delay

    fun setOriginImageBitmap(bitmap: Bitmap) { // origin bitmap (ImageView)
        _originImageBitmap.value = bitmap
    }

    fun clearCanvas() { // clear all
        _mPath.reset()
        _paths = LinkedHashMap()
        _isEraseMode.postValue(false)
        _isPenMode.postValue(false)
        _redraw.postValue(!_redraw.value!!)
    }

    fun changeEraseMode() { // enable or disable eraser
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

    fun changePenMode() { // enable or disable pen
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

        val x = event.x
        val y = event.y
        if (event.pointerCount == 1) { // don't track on multi-touch
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    _startX = x
                    _startY = y
                    _mPath.reset()
                    _mPath.moveTo(x, y)
                    _currentX = x
                    _currentY = y

                    _redraw.postValue(!_redraw.value!!)
                }
                MotionEvent.ACTION_MOVE -> {
                    _mPath.quadTo(_currentX, _currentY, (x + _currentX) / 2, (y + _currentY) / 2)
                    _currentX = x
                    _currentY = y

                    _redraw.postValue(!_redraw.value!!)
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
                    _redraw.postValue(!_redraw.value!!)
                }
            }
        }
        return true
    }

    fun inferMask(image: Bitmap, width: Int, height: Int) {
        // width and height of the image view
        Log.d("CanvasViewModel", "infer mask")
        val request = InferenceUtils.getMaskInferenceRequest(image)
        _maskError.postValue(false)
        viewModelScope.launch {
            var response: InferenceResponse?
            try {
                response = segmentationRepository.inferMask(request)
            } catch (e: Exception) {
                try {
                    Log.e(
                        "CanvasViewModel",
                        "Inference mask failed with exception: ${e.message}, retrying..."
                    )
                    response = segmentationRepository.inferMask(request) // just retry
                } catch (e: Exception) {
                    Log.e("CanvasViewModel", "Inference mask failed")
                    _maskOverallImage.postValue(null)
                    _masks.postValue(emptyList())
                    _labels.postValue(emptyList())
                    _maskError.postValue(true)
                    return@launch
                }
            }

            if (response is InferenceSuccess) {
                Log.d("CanvasViewModel", "Inference mask success: ${response.outputs[2].data}")

                val overallBitmap = BitmapUtils.Base64toBitmap(response.outputs[0].data[0])
                val resizedOverallBitmap = Bitmap.createScaledBitmap(overallBitmap!!, width, height, false)
                _maskOverallImage.postValue(resizedOverallBitmap) // OUTPUT_OVERALL_IMAGE

                val generatedBitmaps = ArrayList<Bitmap>()
                for (i in 0 until response.outputs[1].data.size) {
                    val generatedBase64 = response.outputs[1].data[i]
                    val generatedBitmap = BitmapUtils.Base64toBitmap(generatedBase64)
                    val resizedBitmap = Bitmap.createScaledBitmap(generatedBitmap!!, width, height, false)
                    generatedBitmaps.add(resizedBitmap)
                }
                _masks.postValue(generatedBitmaps) // OUTPUT_MASKS
                _labels.postValue(response.outputs[2].data) // OUTPUT_LABELS
                _maskError.postValue(false)
            }
            else {
                Log.e("CanvasViewModel", "Inference mask failed")
                _maskOverallImage.postValue(null)
                _masks.postValue(emptyList())
                _labels.postValue(emptyList())
                _maskError.postValue(true)
            }
        }
    }

    fun applyFetchedMask(mask: Bitmap, isErase: Boolean = false) {
        Log.d("CanvasViewModel", "mask: ${mask.width} * ${mask.height}")
        if (isErase) _paths.keys.removeIf{ path -> path is Bitmap && (path == mask) } // erase current
        else _paths[mask] = PaintOptions(STROKE_PEN, null) // draw current
        _redraw.postValue(!_redraw.value!!)
    }
}

