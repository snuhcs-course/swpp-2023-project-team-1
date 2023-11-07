package com.project.spire.utils

import android.graphics.Bitmap
import com.project.spire.core.inference.InferenceRepository
import com.project.spire.network.inference.InferenceRequest
import com.project.spire.network.inference.Input
import com.project.spire.ui.create.InferenceViewModel
import com.project.spire.ui.create.InferenceViewModelFactory

object InferenceUtils {

    fun getInferenceRequest(image: Bitmap, mask: Bitmap, prompt: String): InferenceRequest {
        val name = "stable_diffusion"
        val input = listOf(
            Input("INPUT_IMAGE", listOf(1), "BYTES", listOf(BitmapUtils.BitmaptoBase64(image))),
            Input("MASK", listOf(1), "BYTES", listOf(BitmapUtils.BitmaptoBase64(mask))),
            Input("PROMPT", listOf(1), "BYTES", listOf(prompt)),
            Input("NEGATIVE_PROMPT", listOf(1), "BYTES", listOf("")),
            Input("SAMPLES", listOf(1), "INT32", listOf(4)),
            Input("BASE_STEPS", listOf(1), "INT32", listOf(20)),
            Input("REFINER_STEPS", listOf(1), "INT32", listOf(10)),
            Input("GUIDANCE_SCALE_BASE", listOf(1), "FP32", listOf(7.5)),
            Input("GUIDANCE_SCALE_REFINER", listOf(1), "FP32", listOf(7.5)),
            Input("STRENGTH_BASE", listOf(1), "FP32", listOf(0.8)),
            Input("STRENGTH_REFINER", listOf(1), "FP32", listOf(0.4))
        )
        return InferenceRequest(name, input)
    }

    fun getInferenceRequest(prompt: String): InferenceRequest {
        val name = "stable_diffusion"
        val input = listOf(
            Input("PROMPT", listOf(1), "BYTES", listOf(prompt)),
            Input("NEGATIVE_PROMPT", listOf(1), "BYTES", listOf("")),
            Input("SAMPLES", listOf(1), "INT32", listOf(4)),
            Input("BASE_STEPS", listOf(1), "INT32", listOf(20)),
            Input("REFINER_STEPS", listOf(1), "INT32", listOf(10)),
            Input("GUIDANCE_SCALE_BASE", listOf(1), "FP32", listOf(7.5)),
            Input("GUIDANCE_SCALE_REFINER", listOf(1), "FP32", listOf(7.5)),
            Input("STRENGTH_BASE", listOf(1), "FP32", listOf(0.8)),
            Input("STRENGTH_REFINER", listOf(1), "FP32", listOf(0.3))
        )
        return InferenceRequest(name, input)
    }

    // Singleton ViewModel
    // Uses the same ViewModel instance for all activities
    private val inferenceRepository = InferenceRepository()
    private val inferenceViewModelFactory = InferenceViewModelFactory(inferenceRepository)
    val inferenceViewModel = inferenceViewModelFactory.create(InferenceViewModel::class.java)
}