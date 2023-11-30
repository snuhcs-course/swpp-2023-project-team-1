package com.project.spire.network.inference

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SegmentationAPI {
    @POST("models/open_seed/infer")
    suspend fun inferMask(@Body request: InferenceRequest): Response<InferenceSuccess>
}