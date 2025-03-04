package com.example.store.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.store.auth.AuthManager
import com.example.store.R
import com.example.store.databinding.ActivityAuthBinding
import kotlin.math.log

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val linkToReg = binding.linkToRegistration
        val userLogin = binding.userLogin
        val userPassword = binding.userPassword
        val buttonLogin = binding.buttonLogin

        buttonLogin.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val password = userPassword.text.toString().trim()
            val authManager = AuthManager(this)

            if (authManager.loginUser(login, password)) {
                Toast.makeText(this, "Авторизация успешна", Toast.LENGTH_SHORT).show()

                //Сохраняем данные пользователя
                val sharedPreferences = getSharedPreferences("user_pref", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                val token = authManager.generateUserToken()
                val email = authManager.getUserEmail(login)

                editor.putString("user_token", token)
                editor.putBoolean("is_logged_in", true)
                editor.putString("login", login)
                editor.putString("email", email)
                editor.apply()


                val intent = Intent(this, ItemsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                userLogin.text.clear()
                userPassword.text.clear()
            } else
                Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_LONG).show()
        }

        linkToReg.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}