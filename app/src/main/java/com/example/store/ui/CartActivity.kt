package com.example.store.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.store.dataBase.DataBaseItemsHelper
import com.example.store.databinding.ActivityCartBinding
import com.example.store.data.CartItem
import com.example.store.adapter.CartAdapter
import com.example.store.util.CartManager

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var adapter: CartAdapter
    private lateinit var dbHelper: DataBaseItemsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navigationHelper = NavigationHelper(
            this,
            this,
            binding.drawerLayout,
            binding.toolbar,
            binding.navigationView
        )
        navigationHelper.setupNavigationView()

        dbHelper = DataBaseItemsHelper(this, null)

        // Настройка RecyclerView
        binding.cartItemsList.layoutManager = LinearLayoutManager(this)
        adapter = CartAdapter(CartManager.cartItems, this) { cartItem ->
            removeCartItem(cartItem)
        }
        binding.cartItemsList.adapter = adapter

        updateTotalSum()

        binding.payButton.setOnClickListener {
            // Обработка оплаты (пример – можно расширить функционал оплаты)
            Toast.makeText(this, "Оплата прошла успешно", Toast.LENGTH_SHORT).show()
            clearCart()
        }

        binding.clearCartButton.setOnClickListener {
            clearCart()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun removeCartItem(cartItem: CartItem) {
        // Удаляем элемент из корзины
        CartManager.cartItems.remove(cartItem)
        adapter.updateCartItems(CartManager.cartItems)
        updateTotalSum()
        // Возвращаем удалённое количество товара в магазин
        val currentCount = dbHelper.getItemCount(cartItem.item.id)
        dbHelper.setItemCount(cartItem.item.id, currentCount + cartItem.quantity)
    }

    private fun clearCart() {
        // Для каждого элемента из корзины возвращаем товары в магазин
        for (item in CartManager.cartItems) {
            val currentCount = dbHelper.getItemCount(item.item.id)
            dbHelper.setItemCount(item.item.id, currentCount + item.quantity)
        }
        CartManager.clearCart()
        adapter.updateCartItems(CartManager.cartItems)
        updateTotalSum()
    }

    private fun updateTotalSum() {
        val total = CartManager.totalSum()
        binding.totalSumText.text = "Общая сумма: $total ₽"
    }
}
