package com.example.site

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.site.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val linkToReg = binding.linkToRegistration
        val userLogin = binding.userLogin
        val userPassword = binding.userPassword
        val buttonLogin = binding.buttonLogin

        buttonLogin.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val password = userPassword.text.toString().trim()
            val authManager = AuthManager(this)

            if (authManager.loginUser(login, password)) {
                Toast.makeText(this, "Авторизация успешна", Toast.LENGTH_LONG).show()

                userLogin.text.clear()
                userPassword.text.clear()
            } else
                Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_LONG).show()
        }

        linkToReg.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}