package com.project.spire.core.auth

import com.project.spire.network.auth.LoginRequest
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.project.spire.network.RetrofitClient.Companion.authAPI
import com.project.spire.network.auth.LoginError
import com.project.spire.network.auth.LoginResponse
import com.project.spire.network.auth.LoginSuccess

val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "AUTH_DATASTORE")

object AuthPreferenceKeys {
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
}

class AuthRepository {

    /**
     * Login API
     * Returns LoginResult when API is requested */
    suspend fun login(email: String, password: String): LoginResponse {
        val loginRequest = LoginRequest(email, password)
        val response = authAPI.login(loginRequest)
        if (response.isSuccessful) {
            return if (response.code() == 200) {
                Log.d("AuthDataSource", "Login response: ${response.body()}")
                LoginSuccess(
                    response.body()?.accessToken,
                    response.body()?.refreshToken,
                    response.body()?.userId,
                    response.body()?.username
                )
            } else {
                // Failed to login (400)
                Log.d("AuthDataSource", "Login error: ${response.message()}")
                LoginError(response.message())
            }
        } else {
            // Failed to request API
            Log.d("AuthDataSource", "Login error: ${response.message()}")
            return LoginError(response.message())
        }
    }
}