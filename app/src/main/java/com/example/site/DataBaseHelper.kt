package com.example.site

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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

    fun getUser(login: String, password: String): Boolean {
        val db = this.readableDatabase
        val result = db.rawQuery("SELECT * FROM users WHERE login = '$login' AND password = '$password'", null)

        val userExists = result.moveToFirst()
        result.close()
        db.close()

        return userExists
    }
}