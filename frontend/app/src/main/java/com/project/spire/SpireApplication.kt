package com.project.spire

import android.app.Application
import android.content.Intent
import android.util.Log
import com.project.spire.ui.ErrorActivity
import com.project.spire.utils.DataStoreProvider

class SpireApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DataStoreProvider.init(this)
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            catchException(e)
        }
    }

    private fun catchException(e: Throwable) {
        val intent = Intent(this, ErrorActivity::class.java)
        Log.d("SpireApplication", "catchException: ${e.message}, ${e.cause}")
        intent.putExtra(ErrorActivity.ERROR_MSG, e.message)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

}