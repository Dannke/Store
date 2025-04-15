package com.example.store.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.store.R
import com.example.store.data.CartItem

class CartAdapter(
    private var cartItems: MutableList<CartItem>,
    private val context: android.content.Context,
    private val onRemove: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.cart_item_image)
        val title: TextView = view.findViewById(R.id.cart_item_title)
        val price: TextView = view.findViewById(R.id.cart_item_price)
        val removeButton: Button = view.findViewById(R.id.cart_item_remove_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int = cartItems.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.title.text = cartItem.item.title
        holder.price.text = "${cartItem.item.price} ₽ x ${cartItem.quantity}"
        holder.image.setImageResource(cartItem.item.images[0])
        holder.removeButton.setOnClickListener { onRemove(cartItem) }
    }

    fun updateCartItems(newItems: MutableList<CartItem>) {
        cartItems = newItems
        notifyDataSetChanged()
    }
}
