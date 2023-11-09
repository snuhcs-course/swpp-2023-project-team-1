package com.project.spire.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class ConnectionInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val maxRetry = 5;
        var retryCount = 0;
        val originalRequest = chain.request()
        var response = chain.proceed(originalRequest)

        while (!response.isSuccessful && retryCount < maxRetry) {
            try {
                retryCount++
                response.close()
                response = chain.proceed(originalRequest)
                if (response.isSuccessful) return response;
            } catch (e: Exception) {
                Log.e("ConnectionInterceptor", "connection failed, $retryCount try,: ${e.message}")
            }
        }
        return response
    }
}