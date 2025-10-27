package com.cashi.androidapp

import android.app.Application
import com.google.firebase.FirebaseApp

class CashiApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }
    }
}


