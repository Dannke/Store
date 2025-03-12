package com.example.store.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.store.R
import com.example.store.auth.AuthManager
import com.example.store.databinding.ActivityRegisterBinding
import com.example.store.ui.ItemsActivity
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private fun handleRegistrationErrors(errorMessage: String?) {
        binding.loginInputLayout.error = null
        binding.passwordInputLayout.error = null
        binding.emailInputLayout.error = null

        when (errorMessage) {
            "Не все поля заполнены" -> {
                if (binding.userLogin.text.isNullOrEmpty()) binding.loginInputLayout.error = "Заполните поле"
                if (binding.userPassword.text.isNullOrEmpty()) binding.passwordInputLayout.error = "Заполните поле"
                if (binding.userEmail.text.isNullOrEmpty()) binding.emailInputLayout.error = "Заполните поле"
            }
            "Пользователь с таким логином уже существует" -> {
                binding.loginInputLayout.error = errorMessage
            }
            "Некорректный логин. Логин должен содержать от 3 до 18 символов." -> {
                binding.loginInputLayout.error = errorMessage
            }
            "Некорректный пароль. От 3 символов, включая цифры, строчные и заглавные буквы." -> {
                binding.passwordInputLayout.error = errorMessage
            }
            "Некорректный email. Введите email в формате example@example.com." -> {
                binding.emailInputLayout.error = errorMessage
            }
            else -> {
                Snackbar.make(binding.root, "Пользователь успешно зарегистрирован", Snackbar.LENGTH_SHORT).show()
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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
        val loginInputLayout = binding.loginInputLayout
        val passwordInputLayout = binding.passwordInputLayout
        val emailInputLayout = binding.emailInputLayout

        // Загрузка анимаций
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        val scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up)

        // Применение анимаций к элементам интерфейса
        binding.textViewTitle.startAnimation(fadeIn)
        userLogin.startAnimation(slideUp)
        userEmail.startAnimation(slideUp)
        userPassword.startAnimation(slideUp)
        buttonRegister.startAnimation(scaleUp)
        linkToAuth.startAnimation(fadeIn)

        buttonRegister.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val password = userPassword.text.toString().trim()
            val email = userEmail.text.toString().trim()

            // Очистка предыдущих ошибок
            loginInputLayout.error = null
            passwordInputLayout.error = null
            emailInputLayout.error = null

            // Регистрация
            val authManager = AuthManager(this)
            val errorMessage = authManager.registerUser(login, password, email)

            // Обработка ошибок
            handleRegistrationErrors(errorMessage)
        }

        linkToAuth.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }


}