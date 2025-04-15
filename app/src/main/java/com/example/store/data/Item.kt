package com.example.store.data

data class Item(
    val id: Int,
    val title: String,
    val desc: String,
    val price: Int,
    val brand: String,
    val count: Int,
    val images: List<Int>
)