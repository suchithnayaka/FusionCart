package com.example.fusioncart

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class FusionCartApplication : Application() {
    companion object {
        lateinit var database: FirebaseDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()
        database.setPersistenceEnabled(true)  // Enable offline persistence
    }
}
