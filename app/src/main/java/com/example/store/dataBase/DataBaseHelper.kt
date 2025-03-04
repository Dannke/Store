package com.example.store.dataBase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.store.data.User

class DataBaseHelper(private val context: Context, private val factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, "StoreDataBase", factory, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        val query = "CREATE TABLE users (id INT PRIMARY KEY, login TEXT, password TEXT, email TEXT)"
        db!!.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS users")
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
        val cursor = db.rawQuery("SELECT * FROM users WHERE login = ? AND password = ?", arrayOf(login, hashedPassword))
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
}