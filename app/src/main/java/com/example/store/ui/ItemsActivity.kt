package com.example.store.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.store.data.Item
import com.example.store.adapter.ItemsAdapter
import com.example.store.R
import com.example.store.auth.AuthManager
import com.example.store.data.User
import com.example.store.databinding.ActivityItemsBinding

class ItemsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityItemsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val itemsList = binding.itemsList
        val toolBar = binding.toolbar
        val drawerLayout = binding.drawerLayout
        val navigationView = binding.navigationView

        setSupportActionBar(toolBar)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolBar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Обработка кликов в NavigationView
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    // Переход на экран профиля
                }

                R.id.nav_orders -> {
                    // Переход на экран заказов
                }

                R.id.nav_logout -> {
                    val sharedPreferences = getSharedPreferences("user_pref", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.clear()
                    editor.apply()

                    val intent = Intent(this, AuthActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        // Получаем данные из SharedPreferences
        val sharedPreferences = getSharedPreferences("user_pref", MODE_PRIVATE)
        val login = sharedPreferences.getString("login", "")
        val email = sharedPreferences.getString("email", "")


        // Устанавливаем данные в TextView или другие элементы интерфейса
        val headerView = binding.navigationView.getHeaderView(0)
        val usernameTextView = headerView.findViewById<TextView>(R.id.textViewLogin)
        val emailTextView = headerView.findViewById<TextView>(R.id.textViewEmail)

        usernameTextView.text = "Логин: $login"
        emailTextView.text = "Почта: $email"

        val items = arrayListOf<Item>()

        items.add(Item(1, "sofa", "Диван", "Красивый Диван", 30000))
        items.add(Item(2, "desk", "Стол", "Красивый Стол", 15000))
        items.add(Item(3, "chair", "Стул", "Красивый Стул", 10000))

        itemsList.layoutManager = LinearLayoutManager(this)
        itemsList.adapter = ItemsAdapter(items, this)
    }
}