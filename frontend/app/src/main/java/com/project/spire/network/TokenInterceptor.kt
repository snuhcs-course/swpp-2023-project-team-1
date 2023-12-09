package com.project.spire.network

import android.util.Log
import com.project.spire.utils.DataStoreProvider
import com.project.spire.core.auth.AuthRepository
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor : Interceptor {

    companion object {
        private const val MAX_RETRY = 100
    }

    @OptIn(InternalCoroutinesApi::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val authRepository = AuthRepository(DataStoreProvider.authDataStore)
        var retryCount = 0

        while (retryCount < MAX_RETRY) {
            try {
                retryCount++
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
                            response.close()
                            return chain.proceed(newRequest)
                        }
                    }
                }

                return response
            }
            catch (e: Exception) {
                // Connection failed
                runBlocking {
                    delay(2000)
                }
                Log.e("TokenInterceptor", "connection failed, $retryCount try,: ${e.message}")
            }
        }
        // Connection failed
        throw java.lang.Exception("Connection failed")
    }
}