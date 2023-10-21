package com.project.spire.network

import com.project.spire.core.DataStoreProvider
import com.project.spire.core.auth.AuthRepository
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor : Interceptor {
    @OptIn(InternalCoroutinesApi::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val authRepository = AuthRepository(DataStoreProvider.authDataStore)
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)

        if (response.code == 401) {
            synchronized(this) {
                // runBlocking waits for the coroutine to finish
                val refreshSuccess = runBlocking { authRepository.refresh() }
                if (refreshSuccess) {
                    val newAccessToken = runBlocking { authRepository.accessTokenFlow.first() }
                    val newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()
                    return chain.proceed(newRequest)
                }
            }
        }
        return response
    }
}