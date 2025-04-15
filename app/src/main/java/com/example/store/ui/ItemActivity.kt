package com.example.store.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.store.R
import com.example.store.adapter.ImagePagerAdapter
import com.example.store.data.Item
import com.example.store.dataBase.DataBaseItemsHelper
import com.example.store.databinding.ActivityItemBinding
import com.example.store.util.CartManager
import com.google.android.material.snackbar.Snackbar

class ItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityItemBinding
    private lateinit var dbHelper: DataBaseItemsHelper
    private lateinit var currentItem: Item
    private var itemId: Int = -1
    private var currentCount: Int = 0
    private lateinit var images: List<Int> // Список изображений
    private var currentPosition: Int = 0 // Текущая позиция ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DataBaseItemsHelper(this, null)

        itemId = intent.getIntExtra("itemId", -1)
        if (itemId == -1) {
            Toast.makeText(this, "Ошибка: товар не найден", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Установка NavigationView
        setupNavigationView()

        // Загрузка данных о товаре
        loadItemData()

        // Настройка ViewPager для изображений
        setupViewPager()

        // Настройка кнопки "Купить"
        setupBuyButton()
    }

    private fun setupNavigationView() {
        val navigationHelper = NavigationHelper(
            this,
            this,
            binding.drawerLayout,
            binding.toolbar,
            binding.navigationView
        )
        navigationHelper.setupNavigationView()
    }

    private fun loadItemData() {
        val item = dbHelper.getItemById(itemId)
        if (item == null) {
            Toast.makeText(this, "Товар не найден", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        currentItem = item

        binding.itemListTitle.text = item.title
        binding.itemListDesc.text = "Описание: ${item.desc}"
        binding.itemListCategory.text = "Категория: ${item.brand}"
        binding.itemListPrice.text = "${item.price} ₽"

        // Обновляем текущее количество товара
        currentCount = item.count
        updateCountUI()

        // Сохраняем список изображений
        images = item.images.ifEmpty { listOf(R.drawable.sofa) } // Запасное изображение
    }

    private fun setupViewPager() {
        val adapter = ImagePagerAdapter(images)
        binding.viewPager.adapter = adapter

        if (images.size > 1) {
            binding.viewPager.setCurrentItem(1, false) // Начинаем с первого реального элемента
            binding.viewPager.offscreenPageLimit = 3

            binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                private var currentPosition = 0
                private var isScrolling = false

                override fun onPageSelected(position: Int) {
                    currentPosition = position
                }

                override fun onPageScrollStateChanged(state: Int) {
                    when (state) {
                        ViewPager2.SCROLL_STATE_DRAGGING -> isScrolling = true
                        ViewPager2.SCROLL_STATE_IDLE -> {
                            if (isScrolling) {
                                when (currentPosition) {
                                    0 -> binding.viewPager.setCurrentItem(images.size, false)
                                    adapter.itemCount - 1 -> binding.viewPager.setCurrentItem(1, false)
                                }
                                isScrolling = false
                            }
                        }
                        ViewPager2.SCROLL_STATE_SETTLING -> {}
                    }
                }
            })
        }
    }

    private fun setupBuyButton() {
        binding.itemListButtonBuy.setOnClickListener {
            val success = dbHelper.decreaseItemCount(itemId)

            if (success) {
                currentCount -= 1
                updateCountUI()
                Snackbar.make(binding.root, "Товар куплен!", Snackbar.LENGTH_SHORT).show()
                val item = dbHelper.getItemById(itemId)
                if (item != null) {
                    CartManager.addItem(item,1)
                }
            } else {
                Snackbar.make(binding.root, "Товар закончился", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateCountUI() {
        binding.itemListCount.text = "Количество: $currentCount"

        if (currentCount == 0) {
            binding.itemListButtonBuy.isEnabled = false
            binding.itemListButtonBuy.alpha = 0.5f
            binding.itemListButtonBuy.text = "Нет в наличии"
            binding.itemListButtonBuy.setTextColor(ContextCompat.getColor(this, R.color.black))
        } else {
            binding.itemListButtonBuy.isEnabled = true
            binding.itemListButtonBuy.alpha = 1f
            binding.itemListButtonBuy.text = "Купить"
        }
    }
}