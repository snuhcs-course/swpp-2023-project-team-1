package com.project.spire.ui.create

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.spire.core.inference.InferenceRepository
import com.project.spire.network.inference.InferenceSuccess
import com.project.spire.utils.BitmapUtils
import com.project.spire.utils.InferenceUtils
import kotlinx.coroutines.launch

class InferenceViewModel (
    private val inferenceRepository: InferenceRepository
): ViewModel() {

    private val _inferenceResult = MutableLiveData<Bitmap?>().apply {
        value = null
    }
    val inferenceResult = _inferenceResult

    fun infer(image: Bitmap, mask: Bitmap, prompt: String) {
        Log.d("InferenceViewModel", "Input image size: ${image.byteCount}")
        Log.d("InferenceViewModel", "Input mask size: ${mask.byteCount}")
        val request = InferenceUtils.getInferenceRequest(image, mask, prompt)
        viewModelScope.launch {
            val result = inferenceRepository.infer(request)
            if (result is InferenceSuccess) {
                val output = result.outputs[0]
                Log.d("InferenceViewModel", "Inference success: ${output.name}")
                val generatedBase64 = output.data[0]
                val generatedBitmap = BitmapUtils.Base64toBitmap(generatedBase64)
                Log.d("InferenceViewModel", "Output bitmap size: ${generatedBitmap?.byteCount}")
                _inferenceResult.postValue(generatedBitmap)

            } else {
                Log.e("InferenceViewModel", "Inference failed")
                _inferenceResult.postValue(null)
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