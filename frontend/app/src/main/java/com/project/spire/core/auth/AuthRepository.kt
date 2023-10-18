package com.project.spire.core.auth

import com.project.spire.network.auth.request.LoginRequest
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.project.spire.network.RetrofitClient.Companion.authAPI
import com.project.spire.network.auth.response.LoginError
import com.project.spire.network.auth.response.LoginResponse
import com.project.spire.network.auth.response.LoginSuccess
import kotlinx.coroutines.flow.map

val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "AUTH_DATASTORE")

object AuthPreferenceKeys {
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
}

class AuthRepository (private val authDataStore: DataStore<Preferences>) {

    val accessTokenFlow = authDataStore.data.map { it[AuthPreferenceKeys.ACCESS_TOKEN] ?: "" }
    val refreshTokenFlow = authDataStore.data.map { it[AuthPreferenceKeys.REFRESH_TOKEN] ?: "" }
    val isLoggedInFlow = authDataStore.data.map { it[AuthPreferenceKeys.IS_LOGGED_IN] ?: false }

    /**
     * Login API
     * Returns LoginSuccess or LoginError */
    suspend fun login(email: String, password: String): LoginResponse {
        val loginRequest = LoginRequest(email, password)
        val response = authAPI.login(loginRequest)

        return if (response.isSuccessful) {
            val successBody = response.body() as LoginSuccess
            Log.d("AuthRepository", "Login response: ${successBody.username}")

            // Save tokens to datastore
            authDataStore.edit {
                it[AuthPreferenceKeys.ACCESS_TOKEN] = successBody.accessToken!!
                it[AuthPreferenceKeys.REFRESH_TOKEN] = successBody.refreshToken!!
                it[AuthPreferenceKeys.IS_LOGGED_IN] = true
            }
            successBody
        } else {
            return if (response.code() == 401 || response.code() == 404) {
                // Failed to login (400)
                Log.e("AuthRepository", "Login error ${response.code()}: ${response.message()}")
                LoginError(message = response.message())
            } else {
                // Failed to request API
                Log.e("AuthRepository", "Login error ${response.code()}: ${response.message()}")
                LoginError(message = response.message())
            }
        }
    }
    
    /**
     * Logout API
     * Returns true if logout is successful */
    suspend fun logout(accessToken: String): Boolean {
        val response = authAPI.logout("Bearer $accessToken")

        return if (response.isSuccessful) {
            Log.d("AuthRepository", "Logout response: ${response.code()}")
            clearTokens()
            true
        } else {
            Log.e("AuthRepository", "Logout error ${response.code()}: ${response.message()}")
            false
        }
    }

    private suspend fun clearTokens() {
        authDataStore.edit {
            it[AuthPreferenceKeys.ACCESS_TOKEN] = ""
            it[AuthPreferenceKeys.REFRESH_TOKEN] = ""
            it[AuthPreferenceKeys.IS_LOGGED_IN] = false
        }
    }
}