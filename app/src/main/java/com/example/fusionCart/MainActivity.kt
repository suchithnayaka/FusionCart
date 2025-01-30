package com.example.fusioncart

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.fusioncart.ui.screens.LoginScreen
import com.example.fusioncart.ui.screens.MenuScreen
import com.example.fusioncart.ui.screens.RestaurantListScreen
import com.example.fusioncart.ui.theme.FusionCartTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            FirebaseApp.initializeApp(this)
            Log.d("MainActivity", "Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error initializing Firebase: ${e.message}")
        }
        
        enableEdgeToEdge()
        setContent {
            FusionCartTheme {
                var isLoggedIn by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser != null) }
                var selectedRestaurant by remember { mutableStateOf<String?>(null) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (!isLoggedIn) {
                        LoginScreen(
                            onLoginSuccess = { isLoggedIn = true },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else if (selectedRestaurant != null) {
                        MenuScreen(
                            restaurantId = selectedRestaurant!!,
                            onBackClick = { selectedRestaurant = null },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        RestaurantListScreen(
                            onRestaurantClick = { restaurantName -> 
                                selectedRestaurant = restaurantName
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}