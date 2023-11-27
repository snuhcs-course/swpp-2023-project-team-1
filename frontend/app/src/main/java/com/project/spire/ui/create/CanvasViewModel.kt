package com.project.spire.ui.create

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
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

    private val _maskOverallImage = MutableLiveData<Bitmap?>().apply { value = null }
    private val _masks = MutableLiveData<List<Bitmap>>().apply { value = emptyList() }
    private val _labels = MutableLiveData<List<String>>().apply { value = emptyList() }
    private val _maskError = MutableLiveData<Int>().apply { value = 0 }
    // 0: no error, 1: retrying, 2: failed

    val maskOverallImage: LiveData<Bitmap?> get() = _maskOverallImage
    val masks: LiveData<List<Bitmap>> get() = _masks
    val labels: LiveData<List<String>> get() = _labels
    val maskError: LiveData<Int> get() = _maskError


    private var _originImageBitmap = MutableLiveData<Bitmap>()
    val originImageBitmap: LiveData<Bitmap>
        get() = _originImageBitmap

    fun setOriginImageBitmap(bitmap: Bitmap) {
        _originImageBitmap.value = bitmap
    }

    private var _backgroundMaskBitmap = MutableLiveData<Bitmap?>()
    val backgroundMaskBitmap: LiveData<Bitmap?>
        get() = _backgroundMaskBitmap

    private val STROKE_PEN = 60f
    private val STROKE_ERASER = 80f
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

    // mPath를 LiveData로 만들고 mPath를 observe하는 경우 화면 표시에 delay가 발생하여
    // 대신 isDrawing을 observe

    fun clearCanvas() {
        _mPath.reset()
        resetFetchedMask()
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

    fun inferMask(image: Bitmap, width: Int, height: Int) {
        // width and height of the image view
        Log.d("CanvasViewModel", "infer mask")
        val request = InferenceUtils.getMaskInferenceRequest(image)
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
                    _maskError.postValue(1)
                    response = segmentationRepository.inferMask(request) // just retry
                } catch (e: Exception) {
                    Log.e("CanvasViewModel", "Inference mask failed")
                    _maskOverallImage.postValue(null)
                    _masks.postValue(emptyList())
                    _labels.postValue(emptyList())
                    _maskError.postValue(2)
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
                _maskError.postValue(0)
            }
            else {
                Log.e("CanvasViewModel", "Inference mask failed")
                _maskOverallImage.postValue(null)
                _masks.postValue(emptyList())
                _labels.postValue(emptyList())
                _maskError.postValue(2)
            }
        }
    }

    fun applyFetchedMask(mask: Bitmap?) {
        if (mask == null) {
            Log.d("CanvasViewModel", "mask: null")
            _backgroundMaskBitmap.postValue(null)
            return
        }
        else {
            Log.d("CanvasViewModel", "mask: ${mask.width} * ${mask.height}")
            val oldMask = _backgroundMaskBitmap.value
            if (oldMask == null) {
                _backgroundMaskBitmap.postValue(mask)
                return
            }
            val result = Bitmap.createBitmap(oldMask.width, oldMask.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(result)
            canvas.drawBitmap(oldMask, 0f, 0f, null)
            canvas.drawBitmap(mask, 0f, 0f, null)
            // merge two masks
            _backgroundMaskBitmap.postValue(result)
        }
    }

    fun resetFetchedMask() {
        _backgroundMaskBitmap.postValue(null)
        // TODO: set onclick listener
    }

}

class CanvasViewModelFactory(private val segmentationRepository: SegmentationRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CanvasViewModel::class.java)) {
            return CanvasViewModel(segmentationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}