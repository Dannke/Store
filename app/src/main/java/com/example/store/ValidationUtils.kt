package com.example.store

object ValidationUtils {
    fun isValidEmail(email: String): Boolean {
        val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$")
        return regex.matches(email)
    }

    fun isValidLogin(login: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9_]{3,18}$")
        return regex.matches(login)
    }

    fun isValidPassword(password: String): Boolean {
        val regex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{3,}$")
        return regex.matches(password)
    }
}