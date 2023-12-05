package com.project.spire.core.inference

import android.util.Log
import com.project.spire.network.inference.InferenceRequest
import com.project.spire.network.inference.InferenceResponse
import com.project.spire.network.inference.InferenceSuccess
import com.project.spire.network.inference.SegmentationClient

class SegmentationRepository {
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