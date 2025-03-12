package com.example.store.dataBase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.store.data.Item
import com.example.store.data.User

class DataBaseUsersHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
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
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Создание таблицы пользователей
        val createUsersTable = ("CREATE TABLE $TABLE_USERS ("
                + "$COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_USER_LOGIN TEXT, UNIQUE"
                + "$COLUMN_USER_PASSWORD TEXT,"
                + "$COLUMN_USER_EMAIL TEXT)")
        db.execSQL(createUsersTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun addUser(user: User): Boolean {
        return try {
            val values = ContentValues().apply {
                put(COLUMN_USER_LOGIN, user.login)
                put(COLUMN_USER_PASSWORD, user.password)
                put(COLUMN_USER_EMAIL, user.email)
            }
            writableDatabase.insert(TABLE_USERS, null, values) != -1L
        } catch (e: SQLiteConstraintException) {
            false
        } finally {
            writableDatabase.close()
        }
    }

    fun isLoginExists(login: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_USER_LOGIN FROM $TABLE_USERS WHERE $COLUMN_USER_LOGIN = ?",
            arrayOf(login)
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun clearUserTable() {
        val db = this.writableDatabase.apply {
            delete(TABLE_USERS, null, null)
            execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = ?", arrayOf(TABLE_USERS))

            close()
        }
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
}
