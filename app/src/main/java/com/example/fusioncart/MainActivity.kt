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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fusioncart.ui.navigation.BottomNavBar
import com.example.fusioncart.ui.navigation.Screen
import com.example.fusioncart.ui.screens.*
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
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (isLoggedIn) {
                            BottomNavBar(navController)
                        }
                    }
                ) { innerPadding ->
                    if (!isLoggedIn) {
                        LoginScreen(
                            onLoginSuccess = { isLoggedIn = true },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        NavHost(
                            navController = navController,
                            startDestination = "home",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("home") {
                                HomeScreen(
                                    onRestaurantClick = { restaurant ->
                                        navController.navigate("menu/${restaurant.id}/${restaurant.name}")
                                    }
                                )
                            }
                            
                            composable(
                                route = "menu/{restaurantId}/{restaurantName}",
                            ) { backStackEntry ->
                                val restaurantId = backStackEntry.arguments?.getString("restaurantId") ?: return@composable
                                val restaurantName = backStackEntry.arguments?.getString("restaurantName") ?: return@composable
                                MenuScreen(
                                    restaurantId = restaurantId,
                                    restaurantName = restaurantName,
                                    onBackClick = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                            
                            composable(Screen.Profile.route) {
                                ProfileScreen(
                                    onSignOut = {
                                        isLoggedIn = false
                                        navController.navigate("home") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}