package com.example.site

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.site.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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
                Toast.makeText(this, "Пользователь $login успешно зарегистрирован", Toast.LENGTH_LONG).show()
                userLogin.text.clear()
                userPassword.text.clear()
                userEmail.text.clear()
            } else {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        }

        linkToAuth.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }
    }


}