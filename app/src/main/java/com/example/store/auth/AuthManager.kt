package com.example.store.auth

import android.content.Context
import com.example.store.data.User
import com.example.store.dataBase.DataBaseUsersHelper
import java.security.MessageDigest
import java.util.UUID

class AuthManager(private val context: Context) {
    private val dbHelper = DataBaseUsersHelper(context, null)

    fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString() { "%02x".format(it) }
    }

    fun registerUser(login: String, password: String, email: String): String? {
        return when {
            login.isEmpty() || password.isEmpty() || email.isEmpty() -> {
                "Не все поля заполнены"
            }
            dbHelper.isLoginExists(login) -> {
                "Пользователь с таким логином уже существует"
            }
            !ValidationUtils.isValidLogin(login) -> {
                "Некорректный логин. Логин должен содержать от 3 до 18 символов."
            }

            !ValidationUtils.isValidPassword(password) -> {
                "Некорректный пароль. От 3 символов, включая цифры, строчные и заглавные буквы."
            }

            !ValidationUtils.isValidEmail(email) -> {
                "Некорректный email. Введите email в формате example@example.com."
            }

            else -> {
                // Успешная регистрация + юзер в БД
                val hashedPassword = hashPassword(password)
                val user = User(login, hashedPassword, email)
                dbHelper.addUser(user)
                null
            }
        }
    }

    fun loginUser(login: String, password: String): Boolean {
        val hashedPassword = hashPassword(password)
        return dbHelper.getUser(login, hashedPassword)
    }

    fun getUserEmail(login: String): String? {
        return dbHelper.getUserEmailByLogin(login)
    }

    fun generateUserToken(): String {
        return UUID.randomUUID().toString()
    }
}