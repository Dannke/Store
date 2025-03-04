package com.example.store.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.store.auth.AuthManager
import com.example.store.dataBase.DataBaseHelper
import com.example.store.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var authManager: AuthManager
    private lateinit var dbHelper: DataBaseHelper

    private lateinit var textViewLogin: TextView
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textViewLogin = binding.textViewLogin
        editTextEmail = binding.editTextEmail
        editTextPassword = binding.editTextPassword
        val buttonSave = binding.buttonSave
        val buttonLogout = binding.buttonLogout

        authManager = AuthManager(this)
        dbHelper = DataBaseHelper(this, null)

        val navigationHelper = NavigationHelper(
            this,
            this,
            binding.drawerLayout,
            binding.toolbar,
            binding.navigationView
        )
        navigationHelper.setupNavigationView()

        // Загрузка данных пользователя
        loadUserData()

        // Обработка нажатия на кнопку "Сохранить"
        buttonSave.setOnClickListener {
            saveUserData()
        }

        // Обработка нажатия на кнопку "Выйти"
        buttonLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun loadUserData() {
        val sharedPreferences = getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val login = sharedPreferences.getString("login", "")
        val email = sharedPreferences.getString("email", "")

        textViewLogin.text = login
        editTextEmail.setText(email)
    }

    private fun saveUserData() {
        val sharedPreferences = getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val login = sharedPreferences.getString("login", "") ?: return

        val newEmail = editTextEmail.text.toString().trim()
        val newPassword = editTextPassword.text.toString().trim()

        if (newEmail.isEmpty()) {
            Toast.makeText(this, "Почта не может быть пустой", Toast.LENGTH_SHORT).show()
            return
        }

        // Обновляем почту
        dbHelper.updateUserEmail(login, newEmail)

        // Если введён новый пароль, обновляем его
        if (newPassword.isNotEmpty()) {
            val hashedPassword = authManager.hashPassword(newPassword)
            dbHelper.updateUserPassword(login, hashedPassword)
        }

        // Обновляем SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString("email", newEmail)
        editor.apply()

        Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show()
    }

    private fun logoutUser() {
        val sharedPreferences = getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}