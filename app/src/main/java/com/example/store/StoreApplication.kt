package com.example.store

import android.app.Application
import com.example.store.dataBase.DataBaseItemsHelper
import com.example.store.data.Item
import com.example.store.dataBase.DataBaseUsersHelper

class StoreApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val dbHelperItems = DataBaseItemsHelper(this, null)
        val dbHelperUsers = DataBaseUsersHelper(this, null)

        //dbHelperItems.clearItemsTable(
        //dbHelperUsers.clearUserTable()

        // Проверяем, есть ли уже товары в БД
        val items = dbHelperItems.getAllItems()
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
                    2,
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
                ),
                Item(
                    3,
                    "AIR FORCE 1 '07 PRM",
                    "Кеды AIR FORCE 1 '07 NN. Удобные и красивые кроссовки. Приятный дизайн.",
                    19000,
                    "Nike",
                    40,
                    images = listOf(
                        R.drawable.air_force_1_07_prm,
                        R.drawable.air_force_1_07_prm2,
                        R.drawable.air_force_1_07_prm3
                    )
                ),
                Item(
                    4,
                    "New Balance 996",
                    "Кроссовки New Balance из новой коллекции. Красивые, удобные",
                    18000,
                    "New Balance",
                    60,
                    images = listOf(
                        R.drawable.new_balance_996_1,
                        R.drawable.new_balance_996_2,
                        R.drawable.new_balance_996_3,
                    )
                ),
                Item(
                    5,
                    "New balance 2002",
                    "Кроссовки выполнены из натуральной кожи с сетчатым верхом.",
                    19000,
                    "New Balance",
                    25,
                    images = listOf(
                        R.drawable.new_balance_2002_brown1,
                        R.drawable.new_balance_2002_brown2,
                        R.drawable.new_balance_2002_brown3,
                    )
                )
            )

            initialItems.forEach { dbHelperItems.addItem(it) }
        }
    }
}