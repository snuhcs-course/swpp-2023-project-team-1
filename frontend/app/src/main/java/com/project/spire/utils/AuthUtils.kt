package com.project.spire.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.project.spire.core.auth.AuthRepository
import com.project.spire.core.auth.authDataStore
import kotlinx.coroutines.flow.first

/**
 * Provides singleton objects for data stores */
object DataStoreProvider {
    lateinit var authDataStore: DataStore<Preferences>

    fun init(context: Context) {
        authDataStore = context.authDataStore

        // TODO: Add other datastores here
    }
}

object AuthProvider {

    val authRepository: AuthRepository by lazy {
        AuthRepository(DataStoreProvider.authDataStore)
    }
    suspend fun getAccessToken(): String {
        return authRepository.accessTokenFlow.first()
    }

    suspend fun getRefreshToken(): String {
        return authRepository.refreshTokenFlow.first()
    }
}