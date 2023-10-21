package com.project.spire.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.spire.R
import com.project.spire.core.DataStoreProvider
import com.project.spire.core.auth.AuthRepository
import com.project.spire.ui.MainActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AutoLoginActivity : AppCompatActivity() {

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authDataStore = DataStoreProvider.authDataStore
        val authRepository = AuthRepository(authDataStore)
        val context = this

        // Auto login
        GlobalScope.launch {
            val success = authRepository.refresh()
            if (success) {
                // Go to main activity
                val intent = Intent(context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                // Go to login activity
                val intent = Intent(context, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }
}