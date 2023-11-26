package com.project.spire.core.inference

import android.util.Log
import com.project.spire.network.inference.InferenceClient
import com.project.spire.network.inference.InferenceError
import com.project.spire.network.inference.InferenceRequest
import com.project.spire.network.inference.InferenceResponse
import com.project.spire.network.inference.InferenceSuccess
import com.project.spire.network.inference.SegmentationClient

class InferenceRepository {

    /**
     * Inference API
     * Returns InferenceSuccess or InferenceError */
    suspend fun infer(request: InferenceRequest): InferenceResponse {
        Log.d("InferenceRepository", "Inference request received")
        val response = InferenceClient.inferenceAPI.infer(request)

        return if (response.isSuccessful) {
            val successBody = response.body() as InferenceSuccess
            Log.d("InferenceRepository", "Inference response: ${successBody.modelName}")
            successBody
        } else {
            Log.e("InferenceRepository", "Inference error ${response.code()}: ${response.message()}")
            InferenceError(message = response.message())
        }
    }

    /**
     * Status-Check API
     * Returns true if the inference server is ready */
    suspend fun ready(): Boolean {
        val response = InferenceClient.inferenceAPI.ready()

        return if (response.isSuccessful) {
            Log.d("InferenceRepository", "Ready response: ${response.code()}")
            true
        } else {
            Log.e("InferenceRepository", "Ready error ${response.code()}: ${response.message()}")
            false
        }
    }

    suspend fun inferMask(request: InferenceRequest): InferenceResponse? {
        Log.d("InferenceRepository", "Inference Mask request received")
        val response = SegmentationClient.segmentationAPI.inferMask(request)

        return if (response.isSuccessful) {
            val successBody = response.body() as InferenceSuccess
            Log.d("InferenceRepository", "Inference Mask response: ${successBody.modelName}")
            successBody
        } else {
            Log.e("InferenceRepository", "Inference Mask error ${response.code()}: ${response.message()}")
            null
        }
    }
}