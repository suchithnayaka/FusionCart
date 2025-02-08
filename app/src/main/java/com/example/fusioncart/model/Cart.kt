package com.example.fusioncart.model

data class Cart(
    val items: MutableMap<String, CartItem> = mutableMapOf(),
    var currentRestaurantId: String? = null
) {
    val totalAmount: Double
        get() = items.values.sumOf { it.totalPrice }

    val itemCount: Int
        get() = items.values.sumOf { it.quantity }
}
