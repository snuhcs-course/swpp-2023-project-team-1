package com.project.spire.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.project.spire.core.auth.authDataStore

/**
 * Provides singleton objects for data stores */
object DataStoreProvider {
    lateinit var authDataStore: DataStore<Preferences>

    fun init(context: Context) {
        this.authDataStore = context.authDataStore

        // TODO: Add other datastores here
    }
}