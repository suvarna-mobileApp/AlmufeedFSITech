package com.android.almufeed

import android.app.Application
import androidx.viewbinding.BuildConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BookShelfApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
           // Timber.plant(DebugTree())
        }
    }
}