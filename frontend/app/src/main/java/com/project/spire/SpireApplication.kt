package com.project.spire

import android.app.Application
import android.content.Intent
import com.project.spire.core.DataStoreProvider
import com.project.spire.core.auth.AuthRepository
import com.project.spire.ui.MainActivity
import com.project.spire.ui.auth.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SpireApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DataStoreProvider.init(this)
    }
}