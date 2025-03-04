package com.example.store.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.store.databinding.ActivityItemBinding


class ItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navigationHelper = NavigationHelper(
            this,
            this,
            binding.drawerLayout,
            binding.toolbar,
            binding.navigationView
        )
        navigationHelper.setupNavigationView()

        val title = binding.itemListTitle
        val desc = binding.itemListDesc
        val buttonBuy = binding.itemListButtonBuy

        title.text = intent.getStringExtra("itemTitle")
        desc.text = intent.getStringExtra("itemText")

        buttonBuy.setOnClickListener {
            Toast.makeText(this, "Товар куплен!", Toast.LENGTH_LONG).show()
        }
    }
}