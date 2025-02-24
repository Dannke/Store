package com.example.store.auth

import android.content.Context
import com.example.store.data.User
import com.example.store.dataBase.DataBaseHelper
import java.util.UUID

class AuthManager(private val context: Context) {
    private val dbHelper = DataBaseHelper(context, null)

    fun registerUser(login: String, password: String, email: String): String? {
        return when {
            login.isEmpty() || password.isEmpty() || email.isEmpty() -> {
                "Не все поля заполнены"
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
                val user = User(login, password, email)
                dbHelper.addUser(user)
                null
            }
        }
    }

    fun loginUser(login: String, password: String): Boolean {
        return dbHelper.getUser(login, password)
    }

    fun generateUserToken(): String {
        return UUID.randomUUID().toString()
    }
}