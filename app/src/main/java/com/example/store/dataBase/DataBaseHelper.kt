package com.example.store.dataBase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.store.data.Item
import com.example.store.data.User

class DataBaseHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "store.db"
        private const val DATABASE_VERSION = 1

        // Таблица пользователей
        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "id"
        private const val COLUMN_USER_LOGIN = "login"
        private const val COLUMN_USER_PASSWORD = "password"
        private const val COLUMN_USER_EMAIL = "email"

        // Таблица товаров
        private const val TABLE_ITEMS = "items"
        private const val COLUMN_ITEM_ID = "id"
        private const val COLUMN_ITEM_TITLE = "title"
        private const val COLUMN_ITEM_DESC = "description"
        private const val COLUMN_ITEM_PRICE = "price"
        private const val COLUMN_ITEM_CATEGORY = "category"
        private const val COLUMN_ITEM_COUNT = "count"
        private const val COLUMN_ITEM_IMAGES = "images" // Список изображений (можно хранить как JSON)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Создание таблицы пользователей
        val createUsersTable = ("CREATE TABLE $TABLE_USERS ("
                + "$COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_USER_LOGIN TEXT,"
                + "$COLUMN_USER_PASSWORD TEXT,"
                + "$COLUMN_USER_EMAIL TEXT)")
        db.execSQL(createUsersTable)

        // Создание таблицы товаров
        val createItemsTable = ("CREATE TABLE $TABLE_ITEMS ("
                + "$COLUMN_ITEM_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_ITEM_TITLE TEXT,"
                + "$COLUMN_ITEM_DESC TEXT,"
                + "$COLUMN_ITEM_PRICE INTEGER,"
                + "$COLUMN_ITEM_CATEGORY TEXT,"
                + "$COLUMN_ITEM_COUNT INTEGER,"
                + "$COLUMN_ITEM_IMAGES TEXT)") // Список изображений (например, в формате JSON)
        db.execSQL(createItemsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ITEMS")
        onCreate(db)
    }

    fun addUser(user: User) {
        val values = ContentValues()
        values.put("login", user.login)
        values.put("password", user.password)
        values.put("email", user.email)

        val db = this.writableDatabase
        db.insert("users", null, values)

        db.close()
    }

    fun clearUserTable() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM users")
    }

    fun getUser(login: String, hashedPassword: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM users WHERE login = ? AND password = ?",
            arrayOf(login, hashedPassword)
        )
        val userExists = cursor.count > 0
        cursor.close()
        return userExists
    }

    fun getUserEmailByLogin(login: String): String? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT email FROM users WHERE login = ?", arrayOf(login))
        return if (cursor.moveToFirst()) {
            val email = cursor.getString(0)
            cursor.close()
            email
        } else {
            cursor.close()
            null
        }
    }

    fun updateUserEmail(login: String, newEmail: String) {
        val db = this.writableDatabase
        db.execSQL("UPDATE users SET email = ? WHERE login = ?", arrayOf(newEmail, login))
    }

    fun updateUserPassword(login: String, newPassword: String) {
        val db = this.writableDatabase
        db.execSQL("UPDATE users SET password = ? WHERE login = ?", arrayOf(newPassword, login))
    }

    // Добавление товара
    fun addItem(item: Item): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ITEM_TITLE, item.title)
            put(COLUMN_ITEM_DESC, item.desc)
            put(COLUMN_ITEM_PRICE, item.price)
            put(COLUMN_ITEM_CATEGORY, item.category)
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
}
