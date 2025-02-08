package com.example.fusioncart.viewmodel

import androidx.lifecycle.ViewModel
import com.example.fusioncart.model.Cart
import com.example.fusioncart.model.CartItem
import com.example.fusioncart.model.MenuItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CartViewModel : ViewModel() {
    private val _cart = MutableStateFlow(Cart())
    val cart: StateFlow<Cart> = _cart.asStateFlow()

    fun addItem(menuItem: MenuItem, restaurantId: String) {
        _cart.update { currentCart ->
            // If this is the first item, set the restaurant
            if (currentCart.currentRestaurantId == null) {
                currentCart.currentRestaurantId = restaurantId
            }
            
            // Only allow items from the same restaurant
            if (currentCart.currentRestaurantId != restaurantId) {
                return@update currentCart
            }

            val itemId = "${menuItem.name}_${restaurantId}"
            val existingItem = currentCart.items[itemId]
            
            if (existingItem != null) {
                existingItem.quantity++
            } else {
                currentCart.items[itemId] = CartItem(menuItem, 1, restaurantId)
            }
            currentCart
        }
    }

    fun removeItem(menuItem: MenuItem, restaurantId: String) {
        _cart.update { currentCart ->
            val itemId = "${menuItem.name}_${restaurantId}"
            val existingItem = currentCart.items[itemId]
            
            if (existingItem != null) {
                if (existingItem.quantity > 1) {
                    existingItem.quantity--
                } else {
                    currentCart.items.remove(itemId)
                    if (currentCart.items.isEmpty()) {
                        currentCart.currentRestaurantId = null
                    }
                }
            }
            currentCart
        }
    }

    fun clearCart() {
        _cart.value = Cart()
    }
}
