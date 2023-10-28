package com.project.spire.core.inference

import android.util.Log
import androidx.datastore.preferences.core.edit
import com.project.spire.core.auth.AuthPreferenceKeys
import com.project.spire.network.RetrofitClient
import com.project.spire.network.auth.request.LoginRequest
import com.project.spire.network.auth.response.LoginError
import com.project.spire.network.auth.response.LoginResponse
import com.project.spire.network.auth.response.LoginSuccess
import com.project.spire.network.inference.InferenceClient
import com.project.spire.network.inference.InferenceError
import com.project.spire.network.inference.InferenceRequest
import com.project.spire.network.inference.InferenceResponse
import com.project.spire.network.inference.InferenceSuccess
import com.project.spire.network.inference.Input

class InferenceRepository {

    /**
     * Inference API
     * Returns InferenceSuccess or InferenceError */
    suspend fun infer(name: String, input: List<Input>): InferenceResponse {
        val inferRequest = InferenceRequest(name, input)
        val response = InferenceClient.inferenceAPI.infer(inferRequest)

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
}