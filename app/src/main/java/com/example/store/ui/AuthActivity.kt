package com.example.store.ui

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.store.R
import com.example.store.auth.AuthManager
import com.example.store.databinding.ActivityAuthBinding
import com.google.android.material.snackbar.Snackbar

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
        val loginInputLayout = binding.loginInputLayout
        val passwordInputLayout = binding.passwordInputLayout


        // Загрузка анимаций
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        val scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up)

        // Применение анимаций
        binding.textViewTitleAuth.startAnimation(fadeIn)
        userLogin.startAnimation(slideUp)
        userPassword.startAnimation(slideUp)
        buttonLogin.startAnimation(scaleUp)
        linkToReg.startAnimation(fadeIn)

        buttonLogin.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val password = userPassword.text.toString().trim()
            val authManager = AuthManager(this)

            // Очистка предыдущих ошибок
            loginInputLayout.error = null
            passwordInputLayout.error = null

            if (login.isEmpty() || password.isEmpty()) {
                if (login.isEmpty()) loginInputLayout.error = "Заполните поле"
                if (password.isEmpty()) passwordInputLayout.error = "Заполните поле"
                return@setOnClickListener
            }

            if (authManager.loginUser(login, password)) {
                Snackbar.make(binding.root, "Авторизация успешна", Snackbar.LENGTH_SHORT).show()

                // Сохраняем данные пользователя
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


            } else {
                passwordInputLayout.error = "Неверный логин или пароль"
            }
        }

        // Очистка ошибок
        userLogin.addTextChangedListener { loginInputLayout.error = null }
        userPassword.addTextChangedListener { passwordInputLayout.error = null}

        // На регистрацию
        linkToReg.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}