package com.project.spire.core.auth

import com.project.spire.network.auth.request.LoginRequest
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.project.spire.network.RetrofitClient.Companion.authAPI
import com.project.spire.network.auth.response.LoginError
import com.project.spire.network.auth.response.LoginResponse
import com.project.spire.network.auth.response.LoginSuccess

val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "AUTH_DATASTORE")

object AuthPreferenceKeys {
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
}

class AuthRepository {

    /**
     * Login API
     * Returns LoginSuccess or LoginError */
    suspend fun login(email: String, password: String): LoginResponse {
        val loginRequest = LoginRequest(email, password)
        val response = authAPI.login(loginRequest)
        return if (response.isSuccessful) {
            val successBody = response.body() as LoginSuccess
            Log.d("AuthDataSource", "Login response: ${successBody.username}")
            successBody
        } else {
            return if (response.code() == 401 || response.code() == 404) {
                // Failed to login (400)
                Log.e("AuthDataSource", "Login error ${response.code()}: ${response.message()}")
                LoginError(message = response.message())
            } else {
                // Failed to request API
                Log.e("AuthDataSource", "Login error ${response.code()}: ${response.message()}")
                LoginError(message = response.message())
            }
        }
    }
}