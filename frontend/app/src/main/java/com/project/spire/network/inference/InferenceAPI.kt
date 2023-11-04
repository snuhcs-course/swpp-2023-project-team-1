package com.project.spire.network.inference

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface InferenceAPI {
    @GET("health/ready")
    suspend fun ready(): Response<Void>

    @POST("models/stable_diffusion/infer")
    suspend fun infer(@Body request: InferenceRequest): Response<InferenceSuccess>
}