package com.example.store.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.store.data.Item
import com.example.store.adapter.ItemsAdapter
import com.example.store.R
import com.example.store.databinding.ActivityItemsBinding

class ItemsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityItemsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Настройка NavigationView
        val navigationHelper = NavigationHelper(
            this,
            this,
            binding.drawerLayout,
            binding.toolbar,
            binding.navigationView
        )
        navigationHelper.setupNavigationView()

        // Настройка списка товаров
        val itemsList = binding.itemsList
        val items = arrayListOf<Item>()

        items.add(Item(1, "sofa", "Диван", "Красивый Диван", 30000))
        items.add(Item(2, "desk", "Стол", "Красивый Стол", 15000))
        items.add(Item(3, "chair", "Стул", "Красивый Стул", 10000))

        itemsList.layoutManager = LinearLayoutManager(this)
        itemsList.adapter = ItemsAdapter(items, this)
    }
}