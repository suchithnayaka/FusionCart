package com.example.fusioncart

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class FusionCartApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase Database with your URL
        val database = FirebaseDatabase.getInstance()
        database.setPersistenceEnabled(true)  // Enable offline persistence
    }
}
