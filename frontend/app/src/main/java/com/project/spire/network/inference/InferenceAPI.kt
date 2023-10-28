package com.project.spire.network.inference

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface InferenceAPI {
    @GET("health/ready")
    suspend fun ready(): String

    @POST("models/deep_floyd_if/infer")
    suspend fun infer(@Body request: InferenceRequest): String
}