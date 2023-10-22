package com.project.spire.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.spire.core.DataStoreProvider
import com.project.spire.core.auth.AuthRepository
import com.project.spire.ui.MainActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * This Activity is invisible
 * For automated login
 * Must be the initial launch Activity */
class AutoLoginActivity : AppCompatActivity() {

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authDataStore = DataStoreProvider.authDataStore
        val authRepository = AuthRepository(authDataStore)

        // Auto login
        GlobalScope.launch {
            val success = authRepository.refresh()
            if (success) {
                goto<MainActivity>()
            } else {
                goto<LoginActivity>()
            }
        }
    }

    private inline fun <reified T: AppCompatActivity> goto() {
        val intent = Intent(this, T::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}