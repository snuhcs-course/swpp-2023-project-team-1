package com.project.spire

import android.app.Application
import com.project.spire.utils.DataStoreProvider

class SpireApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DataStoreProvider.init(this)
    }
}