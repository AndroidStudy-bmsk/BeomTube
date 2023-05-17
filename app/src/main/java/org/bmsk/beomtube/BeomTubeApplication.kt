package org.bmsk.beomtube

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class BeomTubeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}