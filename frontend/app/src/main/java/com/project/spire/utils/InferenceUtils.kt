package com.project.spire.utils

import android.graphics.Bitmap
import com.project.spire.core.inference.InferenceRepository
import com.project.spire.network.inference.InferenceRequest
import com.project.spire.network.inference.Input
import com.project.spire.ui.create.InferenceViewModel
import com.project.spire.ui.create.InferenceViewModelFactory

object InferenceUtils {

    fun getInferenceRequest(image: Bitmap, mask: Bitmap, prompt: String): InferenceRequest {
        val name = "deep_floyd_if"
        val input = listOf(
            Input("INPUT_IMAGE", listOf(1), "BYTES", listOf(Base64Utils.BitmaptoBase64(image))),
            Input("MASK", listOf(1), "BYTES", listOf(Base64Utils.BitmaptoBase64(mask))),
            Input("PROMPT", listOf(1), "BYTES", listOf(prompt)),
            Input("NEGATIVE_PROMPT", listOf(1), "BYTES", listOf("")),
            Input("SAMPLES", listOf(1), "INT32", listOf(1)),
            Input("STEPS", listOf(1), "INT32", listOf(40)),
            Input("GUIDANCE_SCALE", listOf(1), "FP32", listOf(9.0)),
            Input("STRENGTH", listOf(1), "FP32", listOf(0.999))
        )
        return InferenceRequest(name, input)
    }

    // Singleton ViewModel
    // Uses the same ViewModel instance for all activities
    private val inferenceRepository = InferenceRepository()
    private val inferenceViewModelFactory = InferenceViewModelFactory(inferenceRepository)
    val inferenceViewModel = inferenceViewModelFactory.create(InferenceViewModel::class.java)
}