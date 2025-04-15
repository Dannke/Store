package com.example.store.dataBase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.store.data.Item

class DataBaseItemsHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "store.db"
        private const val DATABASE_VERSION = 2

        // Таблица товаров
        private const val TABLE_ITEMS = "items"
        private const val COLUMN_ITEM_ID = "id"
        private const val COLUMN_ITEM_TITLE = "title"
        private const val COLUMN_ITEM_DESC = "description"
        private const val COLUMN_ITEM_PRICE = "price"
        private const val COLUMN_ITEM_CATEGORY = "category"
        private const val COLUMN_ITEM_COUNT = "count"
        private const val COLUMN_ITEM_IMAGES = "images"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Создание таблицы товаров
        val createItemsTable = ("CREATE TABLE $TABLE_ITEMS ("
                + "$COLUMN_ITEM_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_ITEM_TITLE TEXT,"
                + "$COLUMN_ITEM_DESC TEXT,"
                + "$COLUMN_ITEM_PRICE INTEGER,"
                + "$COLUMN_ITEM_CATEGORY TEXT,"
                + "$COLUMN_ITEM_COUNT INTEGER,"
                + "$COLUMN_ITEM_IMAGES TEXT)")
        db.execSQL(createItemsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ITEMS")
        onCreate(db)
    }

    fun clearItemsTable() {
        val db = this.writableDatabase.apply {
            delete(TABLE_ITEMS, null, null)
            execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = ?", arrayOf(TABLE_ITEMS))

            close()
        }
    }

    // Добавление товара
    fun addItem(item: Item): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ITEM_TITLE, item.title)
            put(COLUMN_ITEM_DESC, item.desc)
            put(COLUMN_ITEM_PRICE, item.price)
            put(COLUMN_ITEM_CATEGORY, item.brand)
            put(COLUMN_ITEM_COUNT, item.count)
            put(COLUMN_ITEM_IMAGES, item.images.joinToString(",")) // Сохраняем изображения как строку
        }
        return db.insert(TABLE_ITEMS, null, values)
    }

    // Получение списка товаров
    fun getAllItems(): List<Item> {
        val items = mutableListOf<Item>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ITEMS", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_TITLE))
                val desc = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_DESC))
                val price = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_PRICE))
                val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_CATEGORY))
                val count = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_COUNT))
                val images = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_IMAGES)).split(",").map { it.toInt() }

                items.add(Item(id, title, desc, price, category, count, images))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return items
    }

    // Получение количества товара
    fun getItemCount(itemId: Int): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_ITEM_COUNT FROM $TABLE_ITEMS WHERE $COLUMN_ITEM_ID = ?",
            arrayOf(itemId.toString())
        )

        return try {
            if (cursor.moveToFirst()) cursor.getInt(0) else -1
        } finally {
            cursor.close()
        }
    }

    // Получение полного объекта Item
    fun getItemById(itemId: Int): Item? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_ITEMS WHERE $COLUMN_ITEM_ID = ?",
            arrayOf(itemId.toString())
        )

        return try {
            if (cursor.moveToFirst()) {
                Item(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_TITLE)),
                    desc = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_DESC)),
                    price = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_PRICE)),
                    brand = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_CATEGORY)),
                    count = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_COUNT)),
                    images = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_IMAGES))
                        .split(",").map { it.toInt() }
                )
            } else null
        } finally {
            cursor.close()
        }
    }

    // Уменьшение количества товара
    fun decreaseItemCount(itemId: Int): Boolean {
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_ITEM_COUNT FROM $TABLE_ITEMS WHERE $COLUMN_ITEM_ID = ?", arrayOf(itemId.toString()))

        return if (cursor.moveToFirst()) {
            val currentCount = cursor.getInt(0)
            if (currentCount > 0) {
                val newCount = currentCount - 1
                db.execSQL("UPDATE $TABLE_ITEMS SET $COLUMN_ITEM_COUNT = ? WHERE $COLUMN_ITEM_ID = ?", arrayOf(newCount, itemId))
                true // Товар успешно куплен
            } else {
                false // Товар закончился
            }
        } else {
            false // Товар не найден
        }.also {
            cursor.close()
        }
    }

    // Метод для установки количества товара
    fun setItemCount(itemId: Int, newCount: Int) {
        val db = this.writableDatabase
        db.execSQL(
            "UPDATE $TABLE_ITEMS SET $COLUMN_ITEM_COUNT = ? WHERE $COLUMN_ITEM_ID = ?",
            arrayOf(newCount.toString(), itemId.toString())
        )
        db.close()
    }
}
