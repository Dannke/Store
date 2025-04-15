package com.example.store.util

import com.example.store.data.CartItem
import com.example.store.data.Item

object CartManager {
    val cartItems = mutableListOf<CartItem>()

    fun addItem(item: Item, quantity: Int = 1) {
        val existing = cartItems.find { it.item.id == item.id }
        if (existing != null) {
            existing.quantity += quantity
        } else {
            cartItems.add(CartItem(item, quantity))
        }
    }

    fun removeItem(item: Item) {
        cartItems.removeAll { it.item.id == item.id }
    }

    fun clearCart() {
        cartItems.clear()
    }

    fun totalSum(): Int {
        return cartItems.sumOf { it.item.price * it.quantity }
    }
}