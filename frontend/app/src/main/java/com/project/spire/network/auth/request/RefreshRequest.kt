package com.project.spire.network.auth.request

data class RefreshRequest (
    val accessToken: String,
    val refreshToken: String,
)