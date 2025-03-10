package com.example.store.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.store.R
import com.example.store.adapter.ImagePagerAdapter
import com.example.store.databinding.ActivityItemBinding
import com.google.android.material.snackbar.Snackbar

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

        // Получаем данные из Intent
        val title = intent.getStringExtra("itemTitle")
        val desc = intent.getStringExtra("itemDesc")
        val category = intent.getStringExtra("itemCategory")
        val count = intent.getIntExtra("itemCount", 0)
        val price = intent.getIntExtra("itemPrice", 0)
        val image = intent.getStringExtra("itemImage")

        // Устанавливаем данные в элементы интерфейса
        binding.itemListTitle.text = title
        binding.itemListDesc.text = "Описание: $desc"
        binding.itemListCategory.text = "Категория: $category"
        binding.itemListCount.text = "Количество: $count"
        binding.itemListPrice.text = "$price ₽"

// Получаем список изображений из Intent
        val images = intent.getIntegerArrayListExtra("itemImages") ?: listOf(
            R.drawable.sofa // Запасное изображение, если список пуст
        )

// Передаём список изображений в адаптер
        val adapter = ImagePagerAdapter(images)
        binding.viewPager.adapter = adapter

        binding.viewPager.setCurrentItem(1, false)

        // Обработка перехода на реальные элементы
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val adapter = binding.viewPager.adapter as ImagePagerAdapter
                when (position) {
                    0 -> {
                        // Переход на последний элемент
                        binding.viewPager.setCurrentItem(adapter.itemCount - 2, false)
                    }

                    adapter.itemCount - 1 -> {
                        // Переход на первый элемент
                        binding.viewPager.setCurrentItem(1, false)
                    }
                }
            }
        })

        // Проверка количества товара
        if (count == 0) {
            binding.itemListButtonBuy.isEnabled = false
            binding.itemListButtonBuy.alpha = 0.5f
            binding.itemListButtonBuy.text = "Нет в наличии"
            binding.itemListButtonBuy.setTextColor(ContextCompat.getColor(this, R.color.black))
        } else {
            binding.itemListButtonBuy.isEnabled = true
            binding.itemListButtonBuy.alpha = 1f
            binding.itemListButtonBuy.text = "Купить"
        }

        // Обработка нажатия на кнопку "Купить"
        binding.itemListButtonBuy.setOnClickListener {
            Snackbar.make(binding.root, "Товар куплен!", Snackbar.LENGTH_SHORT).show()
        }
    }
}