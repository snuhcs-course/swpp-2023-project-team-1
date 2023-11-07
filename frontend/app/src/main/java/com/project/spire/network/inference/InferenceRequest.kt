package com.project.spire.network.inference

import com.google.gson.annotations.SerializedName

data class InferenceRequest (
    @SerializedName("name")
    val name: String,

    @SerializedName("inputs")
    val input: List<Input>
)

data class Input (
    // INPUT_IMAGE, MASK, PROMPT, NEGATIVE_PROMPT,
    // SAMPLES, BASE_STEPS, REFINER_STEPS, GUIDANCE_SCALE_BASE
    // GUIDANCE_SCALE_REFINER, STRENGTH_BASE, STRENGTH_REFINER
    @SerializedName("name")
    val name: String,

    @SerializedName("shape")
    val shape: List<Int> = listOf(1),

    @SerializedName("datatype")
    val datatype: String = "BYTES",

    // Base64, String, Int, Float, etc.
    @SerializedName("data")
    val data: List<Any>
)