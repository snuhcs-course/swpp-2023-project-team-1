package com.project.spire.network.inference

import com.google.gson.annotations.SerializedName

interface InferenceResponse {
}

data class InferenceSuccess (
    @SerializedName("model_name")
    val modelName: String,

    @SerializedName("model_version")
    val modelVersion: String,

    @SerializedName("outputs")
    var outputs: List<Output> // TODO: parse this

) : InferenceResponse

data class InferenceError(

    @SerializedName("error")
    val message: String

) : InferenceResponse

data class Output (
    // OUTPUT_IMAGE
    @SerializedName("name")
    val name: String,

    @SerializedName("datatype")
    val datatype: String,

    @SerializedName("shape")
    val shape: List<Int>,

    @SerializedName("data")
    val data: List<String>
)
