package com.project.spire

import android.app.Application
import com.project.spire.core.DataStoreProvider

class SpireApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DataStoreProvider.init(this)
    }
}