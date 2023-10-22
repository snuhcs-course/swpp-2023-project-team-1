package com.project.spire.network.auth.request

import com.google.gson.annotations.SerializedName

data class EmailRequest (

    @SerializedName("email")
    val email: List<String>,
)