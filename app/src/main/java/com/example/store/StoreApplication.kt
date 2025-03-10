package com.example.store

import android.app.Application
import com.example.store.dataBase.DataBaseHelper
import com.example.store.data.Item

class StoreApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val dbHelper = DataBaseHelper(this, null)

        // Проверяем, есть ли уже товары в БД
        val items = dbHelper.getAllItems()
        if (items.isEmpty()) {
            // Добавляем товары, если БД пуста
            val initialItems = listOf(
                Item(
                    1,
                    "Nike Court Vision Low Next Nature",
                    "Кеды Nike Court Vision Low Next Nature. Стильные, удобные, красивые",
                    15000,
                    "Nike",
                    100,
                    images = listOf(
                        R.drawable.nike_court,
                        R.drawable.nike_court1,
                        R.drawable.nike_court2
                    )
                ),
                Item(
                    1,
                    "AIR FORCE 1 '07 NN",
                    "Кеды AIR FORCE 1 '07 NN. Удобные и красивые кроссовки.",
                    20000,
                    "Nike",
                    0,
                    images = listOf(
                        R.drawable.air_force,
                        R.drawable.air_force_1_07_2,
                        R.drawable.air_force_1_07_3
                    )
                )
            )

            initialItems.forEach { dbHelper.addItem(it) }
        }
    }
}