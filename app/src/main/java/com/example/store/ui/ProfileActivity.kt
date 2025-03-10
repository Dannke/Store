package com.example.store.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.store.R
import com.example.store.auth.AuthManager
import com.example.store.auth.ValidationUtils
import com.example.store.dataBase.DataBaseHelper
import com.example.store.databinding.ActivityProfileBinding
import com.google.android.material.snackbar.Snackbar

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var authManager: AuthManager
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var textViewLogin: TextView
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextPasswordRepeat: EditText
    private lateinit var buttonEdit: Button
    private lateinit var buttonSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация элементов
        textViewLogin = binding.textViewLogin
        editTextEmail = binding.editTextEmail
        editTextPassword = binding.editTextPassword
        editTextPasswordRepeat = binding.editTextPasswordRepeat
        buttonEdit = binding.buttonEdit
        buttonSave = binding.buttonSave

        val emailInputLayout = binding.emailInputLayout
        val passwordInputLayout = binding.passwordInputLayout
        val passwordRepeatInputLayout = binding.passwordRepeatInputLayout
        val passwordFieldsContainer = binding.passwordFieldsContainer

        // Инициализация AuthManager и DataBaseHelper
        authManager = AuthManager(this)
        dbHelper = DataBaseHelper(this, null)

        // Настройка NavigationView
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

        // Анимации
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        textViewLogin.startAnimation(fadeIn)
        editTextEmail.startAnimation(slideUp)
        buttonEdit.startAnimation(fadeIn)
        buttonSave.startAnimation(fadeIn)

        // Обработка нажатия на кнопку "Редактировать"
        buttonEdit.setOnClickListener {
            if (buttonEdit.text == "Редактировать") {
                // Включение режима редактирования
                editTextEmail.isEnabled = true
                buttonEdit.text = "Отменить"
                buttonSave.isEnabled = false
                buttonSave.alpha = 0.5f
                passwordFieldsContainer.visibility = View.VISIBLE
            } else {
                // Отмена редактирования
                editTextEmail.isEnabled = false
                buttonEdit.text = "Редактировать"
                buttonSave.isEnabled = false
                buttonSave.alpha = 0.5f
                passwordFieldsContainer.visibility = View.GONE
                loadUserData() // Восстанавливаем исходные данные
            }
        }

        // Обработка нажатия на кнопку "Сохранить"
        buttonSave.setOnClickListener {
            if (saveUserData()) {
                // Успешное сохранение
                editTextEmail.isEnabled = false
                buttonEdit.text = "Редактировать"
                buttonSave.isEnabled = false
                buttonSave.alpha = 0.5f
                passwordFieldsContainer.visibility = View.GONE
            }
        }

        // Слушатели изменений в полях
        editTextEmail.addTextChangedListener(createTextWatcher { checkChanges() })
        editTextPassword.addTextChangedListener(createTextWatcher { checkChanges() })
        editTextPasswordRepeat.addTextChangedListener(createTextWatcher { checkChanges() })

        // Очистка ошибок при вводе
        editTextEmail.addTextChangedListener(createTextWatcher { emailInputLayout.error = null })
        editTextPassword.addTextChangedListener(createTextWatcher { passwordInputLayout.error = null })
        editTextPasswordRepeat.addTextChangedListener(createTextWatcher { passwordRepeatInputLayout.error = null })
    }

    private fun loadUserData() {
        val sharedPreferences = getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val login = sharedPreferences.getString("login", "")
        val email = sharedPreferences.getString("email", "")

        textViewLogin.text = login
        editTextEmail.setText(email)
        editTextPassword.text?.clear()
        editTextPasswordRepeat.text?.clear()
    }

    private fun saveUserData(): Boolean {
        val sharedPreferences = getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val login = sharedPreferences.getString("login", "") ?: return false

        val newEmail = editTextEmail.text.toString().trim()
        val newPassword = editTextPassword.text.toString().trim()
        val newPasswordRepeat = editTextPasswordRepeat.text.toString().trim()

        // Очистка предыдущих ошибок
        binding.emailInputLayout.error = null
        binding.passwordInputLayout.error = null
        binding.passwordRepeatInputLayout.error = null

        // Валидация данных
        if (newEmail.isEmpty() && newPassword.isEmpty() && newPasswordRepeat.isEmpty()) {
            Snackbar.make(binding.root, "Нет изменений для сохранения", Snackbar.LENGTH_SHORT).show()
            return false
        }

        if (newEmail.isNotEmpty() && !ValidationUtils.isValidEmail(newEmail)) {
            binding.emailInputLayout.error = "Некорректный email. Введите email в формате example@example.com."
            return false
        }

        if (newPassword.isNotEmpty() && !ValidationUtils.isValidPassword(newPassword)) {
            binding.passwordInputLayout.error = "Некорректный пароль. От 3 символов, включая цифры, строчные и заглавные буквы."
            return false
        }

        if (newPassword != newPasswordRepeat) {
            binding.passwordRepeatInputLayout.error = "Пароли не совпадают"
            return false
        }

        // Обновляем почту
        if (newEmail.isNotEmpty()) {
            dbHelper.updateUserEmail(login, newEmail)
        }

        // Если введён новый пароль, обновляем его
        if (newPassword.isNotEmpty()) {
            val hashedPassword = authManager.hashPassword(newPassword)
            dbHelper.updateUserPassword(login, hashedPassword)
        }

        // Обновляем SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString("email", newEmail)
        editor.apply()

        Snackbar.make(binding.root, "Данные сохранены", Snackbar.LENGTH_SHORT).show()
        return true
    }

    private fun checkChanges() {
        val newEmail = editTextEmail.text.toString().trim()
        val newPassword = editTextPassword.text.toString().trim()
        val newPasswordRepeat = editTextPasswordRepeat.text.toString().trim()

        val isEmailChanged = newEmail != getSharedPreferences("user_pref", Context.MODE_PRIVATE)
            .getString("email", "")
        val isPasswordChanged = newPassword.isNotEmpty() || newPasswordRepeat.isNotEmpty()

        buttonSave.isEnabled = isEmailChanged || isPasswordChanged
        buttonSave.alpha = if (buttonSave.isEnabled) 1f else 0.5f
    }

    private fun createTextWatcher(onTextChanged: () -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onTextChanged()
            }
            override fun afterTextChanged(s: Editable?) {}
        }
    }
}