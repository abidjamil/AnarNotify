package com.anaar.io.notify

import android.app.Application
import android.content.Intent
import android.os.Build

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        val serviceIntent = Intent(this, KeepAliveService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

    }
}