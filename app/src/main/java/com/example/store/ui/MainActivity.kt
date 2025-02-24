package com.example.store.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.store.auth.AuthManager
import com.example.store.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Проверка, авторизован ли пользователь
        val sharedPreferences = getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        val userToken = sharedPreferences.getString("user_token", null)

        if (isLoggedIn && userToken != null) {
            // Пользователь авторизован
            val intent = Intent(this, ItemsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        val userLogin = binding.userLogin
        val userPassword = binding.userPassword
        val userEmail = binding.userEmail
        val buttonRegister = binding.buttonReg
        val linkToAuth = binding.linkToAuth

        buttonRegister.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val password = userPassword.text.toString().trim()
            val email = userEmail.text.toString().trim()

            val authManager = AuthManager(this)
            val errorMessage = authManager.registerUser(login, password, email)

            if (errorMessage == null) {
                Toast.makeText(
                    this,
                    "Пользователь $login успешно зарегистрирован",
                    Toast.LENGTH_LONG
                ).show()

                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)

                userLogin.text.clear()
                userPassword.text.clear()
                userEmail.text.clear()
            } else {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        }

        linkToAuth.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }


}