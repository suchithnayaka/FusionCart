package com.example.fusioncart.model

data class CartItem(
    val menuItem: MenuItem,
    var quantity: Int = 1,
    val restaurantId: String
) {
    val totalPrice: Double
        get() = menuItem.price.toDoubleOrNull()?.times(quantity) ?: 0.0
}
