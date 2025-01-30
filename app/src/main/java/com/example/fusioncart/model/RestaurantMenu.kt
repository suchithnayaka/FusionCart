package com.example.fusioncart.model

data class RestaurantMenu(
    val restaurantId: String = "",
    val items: List<MenuItem> = emptyList()
)
