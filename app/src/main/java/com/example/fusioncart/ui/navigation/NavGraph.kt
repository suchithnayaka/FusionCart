package com.example.fusioncart.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fusioncart.ui.screens.HomeScreen
import com.example.fusioncart.ui.screens.MenuScreen
import com.example.fusioncart.model.Restaurant

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = "home"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("home") {
            HomeScreen(
                onRestaurantClick = { restaurant: Restaurant ->
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
    }
}
