package com.example.fusioncart.model

data class Restaurant(
    val id: String = "",
    val name: String = "",
    val image: String = "",
    val location: String = "",
    val rating: Double = 0.0,
    val cuisine: String = "",
    val deliveryTime: Int = 30,
    val costForTwo: Int = 0,
    val isVeg: Boolean = false,
    val tag: String = ""
)
