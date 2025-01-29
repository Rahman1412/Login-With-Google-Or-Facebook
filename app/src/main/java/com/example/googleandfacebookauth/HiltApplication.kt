package com.example.googleandfacebookauth

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HiltApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(this)
        AppEventsLogger.activateApp(this)
    }
}