package com.example.store.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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
        enableEdgeToEdge()
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
        updateCartState()
        switchToItems()

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
        // Уменьшаем количество на 1
        if (cartItem.quantity > 1) {
            cartItem.quantity--
            val currentCount = dbHelper.getItemCount(cartItem.item.id)
            dbHelper.setItemCount(cartItem.item.id, currentCount + 1)
            // Обновляем список в адаптере
            adapter.updateCartItems(CartManager.cartItems)
        } else {
            // Если это была последняя штука — удаляем весь объект
            CartManager.cartItems.remove(cartItem)
            adapter.updateCartItems(CartManager.cartItems)
            val currentCount = dbHelper.getItemCount(cartItem.item.id)
            dbHelper.setItemCount(cartItem.item.id, currentCount + 1)
        }
        updateTotalSum()
        updateCartState()
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
        updateCartState()
    }

    private fun updateTotalSum() {
        val total = CartManager.totalSum()
        binding.totalSumText.text = "Общая сумма: $total ₽"
    }

    private fun updateCartState() {
        val isEmpty = CartManager.cartItems.isEmpty()
        binding.emptyCartText.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.linkToItems.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.cartItemsList.visibility = if (isEmpty) View.GONE else View.VISIBLE
        // Кнопки
        binding.clearCartButton.isEnabled = !isEmpty
        binding.payButton.isEnabled = !isEmpty
        binding.clearCartButton.alpha = if (isEmpty) 0.5f else 1f
        binding.payButton.alpha = if (isEmpty) 0.5f else 1f
    }

    private fun switchToItems() {
        val linkToItems = binding.linkToItems

        linkToItems.setOnClickListener {
            val intent = Intent(this, ItemsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}
