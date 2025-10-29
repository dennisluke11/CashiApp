package com.cashi.androidapp

import android.app.Application
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CashiApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }
        
        // Initialize Koin
        startKoin {
            androidContext(this@CashiApplication)
            modules(com.cashi.shared.di.sharedModule)
        }
    }
}


