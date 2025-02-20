package com.example.site

import android.content.Context

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
                val user = User(login, password, email)
                dbHelper.addUser(user)
                null // Успешная регистрация
            }
        }
    }

    fun loginUser(login: String, password: String): Boolean {
        return dbHelper.getUser(login, password)
    }
}