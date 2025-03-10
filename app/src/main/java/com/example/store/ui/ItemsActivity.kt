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

        val items = listOf(
            Item(
                1,
                "nike_court",
                "Nike Court Vision Low Next Nature",
                "Кеды Nike Court Vision Low Next Nature. Стильные, удобные, красивые",
                "Nike",
                15000,
                100,
                images = listOf(
                    R.drawable.nike_court,
                    R.drawable.nike_court1,
                    R.drawable.nike_court2
                )
            ),
            Item(
                1,
                "air_force",
                "AIR FORCE 1 '07 NN",
                "Кеды AIR FORCE 1 '07 NN. Удобные и красивые кроссовки.",
                "Nike",
                20000,
                0,
                images = listOf(
                    R.drawable.air_force,
                    R.drawable.air_force_1_07_2,
                    R.drawable.air_force_1_07_3
                )
            )
        )

        itemsList.layoutManager = LinearLayoutManager(this)
        itemsList.adapter = ItemsAdapter(items, this)
    }
}