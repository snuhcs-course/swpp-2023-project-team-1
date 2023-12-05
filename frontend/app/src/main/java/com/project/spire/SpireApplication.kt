package com.project.spire

import android.app.AlertDialog
import android.app.Application
import com.project.spire.network.TokenInterceptor
import com.project.spire.utils.DataStoreProvider
import kotlinx.coroutines.delay

class SpireApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DataStoreProvider.init(this)
    }

    fun connectionLostDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Connection lost")
        builder.setMessage("Please check your internet connection and try again")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
}