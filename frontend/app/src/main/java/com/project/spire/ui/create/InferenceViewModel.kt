package com.project.spire.ui.create

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.spire.core.inference.InferenceRepository
import com.project.spire.network.inference.InferenceResponse
import com.project.spire.network.inference.InferenceSuccess
import com.project.spire.utils.BitmapUtils
import com.project.spire.utils.InferenceUtils
import kotlinx.coroutines.launch

sealed interface Inference {
    val prompt: String
}

data class Txt2Img(
    override val prompt: String
): Inference

data class Inpainting(
    val image: Bitmap,
    val mask: Bitmap,
    override val prompt: String
): Inference

class InferenceViewModel (
    private val inferenceRepository: InferenceRepository
): ViewModel() {

    private val _inferenceResult = MutableLiveData<ArrayList<Bitmap>?>().apply { value = null }
    private val _previousInference = MutableLiveData<Inference?>().apply { value = null }
    private val _inferenceError = MutableLiveData<Boolean>().apply { value = false }

    val inferenceResult: LiveData<ArrayList<Bitmap>?> get() = _inferenceResult
    val previousInference: LiveData<Inference?> get() = _previousInference
    val inferenceError: LiveData<Boolean> get() = _inferenceError

    fun reset() {
        _inferenceResult.value = null
        Log.d("InferenceViewModel", "Reset LiveData")
        // TODO: how to stall infer()?
    }

    fun infer(image: Bitmap, mask: Bitmap, prompt: String) {
        _previousInference.value = Inpainting(image, mask, prompt)
        Log.d("InferenceViewModel", "Input image size: ${image.byteCount}")
        Log.d("InferenceViewModel", "Input mask size: ${mask.byteCount}")
        val request = InferenceUtils.getInferenceRequest(image, mask, prompt)
        viewModelScope.launch {
            var result: InferenceResponse

            try {
                result = inferenceRepository.infer(request)
            } catch (e: Exception) {
                try {
                    Log.e("InferenceViewModel", "Inference failed with exception: ${e.message}, retrying...")
                    result = inferenceRepository.infer(request) // just retry
                } catch (e: Exception) {
                    Log.e("InferenceViewModel", "Inference failed")
                    _inferenceResult.postValue(null)
                    return@launch
                }
            }

            if (result is InferenceSuccess) {
                val output = result.outputs[0]
                Log.d("InferenceViewModel", "Inference success: ${output.name}")

                val generatedBitmaps = ArrayList<Bitmap>()
                for (i in 0 until output.data.size) {
                    val generatedBase64 = output.data[i]
                    val generatedBitmap = BitmapUtils.Base64toBitmap(generatedBase64)
                    Log.d("InferenceViewModel", "Output $i bitmap size: ${generatedBitmap?.byteCount}")
                    generatedBitmaps.add(generatedBitmap!!)
                }
                _inferenceResult.postValue(generatedBitmaps)
            } else {
                Log.e("InferenceViewModel", "Inference failed")
                _inferenceResult.postValue(null)
                _inferenceError.postValue(true)
            }
        }
    }

    fun infer(prompt: String) {
        _previousInference.value = Txt2Img(prompt)
        Log.d("InferenceViewModel", "Input prompt: $prompt")
        val request = InferenceUtils.getInferenceRequest(prompt)
        viewModelScope.launch {
            var result: InferenceResponse

            try {
                result = inferenceRepository.infer(request)
            } catch (e: Exception) {
                try {
                    Log.e("InferenceViewModel", "Inference failed with exception: ${e.message}, retrying...")
                    result = inferenceRepository.infer(request) // just retry
                } catch (e: Exception) {
                    Log.e("InferenceViewModel", "Inference failed")
                    _inferenceResult.postValue(null)
                    return@launch
                }
            }
            if (result is InferenceSuccess) {
                val output = result.outputs[0]
                Log.d("InferenceViewModel", "Inference success: ${output.name}")

                val generatedBitmaps = ArrayList<Bitmap>()
                for (i in 0 until output.data.size) {
                    val generatedBase64 = output.data[i]
                    val generatedBitmap = BitmapUtils.Base64toBitmap(generatedBase64)
                    Log.d("InferenceViewModel", "Output $i bitmap size: ${generatedBitmap?.byteCount}")
                    generatedBitmaps.add(generatedBitmap!!)
                }
                _inferenceResult.postValue(generatedBitmaps)

            } else {
                Log.e("InferenceViewModel", "Inference failed")
                _inferenceResult.postValue(null)
                _inferenceError.postValue(true)
            }
        }
    }

    fun postUpload() {
        // TODO: Send post upload request
    }
}


class InferenceViewModelFactory(private val inferenceRepository: InferenceRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InferenceViewModel::class.java)) {
            return InferenceViewModel(inferenceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}