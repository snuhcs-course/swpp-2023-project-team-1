package com.project.spire.network.auth.response

import com.google.gson.annotations.SerializedName

interface CheckResponse {
}

data class CheckSuccess (
    @SerializedName("email_exists")
    var emailExists: Boolean,

    @SerializedName("username_exists")
    var usernameExists: Boolean
) : CheckResponse

data class CheckError (
    @SerializedName("message")
    var message: String? = null
) : CheckResponse