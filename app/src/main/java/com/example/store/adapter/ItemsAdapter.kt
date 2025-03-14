package com.example.store.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.store.R
import com.example.store.data.Item
import com.example.store.ui.ItemActivity

class ItemsAdapter(var items: List<Item>, var context: Context) :
    RecyclerView.Adapter<ItemsAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.item_list_image)
        val title: TextView = view.findViewById(R.id.item_list_title)
        val desc: TextView = view.findViewById(R.id.item_list_desc)
        val price: TextView = view.findViewById(R.id.item_list_price)
        val btn: Button = view.findViewById(R.id.item_list_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_in_list, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: List<Item>) {
        items = newItems
        notifyDataSetChanged()
    }

    @SuppressLint("DiscouragedApi")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]

        holder.title.text = items[position].title
        holder.desc.text = items[position].desc
        holder.price.text = items[position].price.toString() + " ₽"

        holder.image.setImageResource(item.images[0])

        holder.btn.setOnClickListener {
            val intent = Intent(context, ItemActivity::class.java)
            intent.putExtra("itemId", item.id)
            intent.putExtra("itemTitle", items[position].title)
            intent.putExtra("itemDesc", items[position].desc)
            intent.putExtra("itemCategory", items[position].category)
            intent.putExtra("itemCount", items[position].count)
            intent.putExtra("itemPrice", items[position].price)
            intent.putIntegerArrayListExtra("itemImages", ArrayList(item.images))

            context.startActivity(intent)
        }
    }
}