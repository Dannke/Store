package com.example.store.ui

import android.content.Context
import android.content.Intent
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.store.R
import com.google.android.material.navigation.NavigationView

class NavigationHelper(
    private val context: Context,
    private val activity: AppCompatActivity,
    private val drawerLayout: DrawerLayout,
    private val toolbar: Toolbar,
    private val navigationView: NavigationView,
) {

    fun setupNavigationView() {
        activity.setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            activity,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Обработка кликов в NavigationView
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_items -> {
                    if (activity !is ItemsActivity) {
                        val intent = Intent(context, ItemsActivity::class.java)
                        context.startActivity(intent)
                    }
                }

                R.id.nav_profile -> {
                    if (activity !is ProfileActivity) {
                        val intent = Intent(context, ProfileActivity::class.java)
                        context.startActivity(intent)
                    }
                }

                R.id.nav_orders -> {
                    // Переход на экран заказов
                }

                R.id.nav_logout -> {
                    logoutUser()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        setUserDataInHeader()
    }

    private fun setUserDataInHeader() {
        val sharedPreferences = context.getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val login = sharedPreferences.getString("login", "")
        val email = sharedPreferences.getString("email", "")

        val headerView = navigationView.getHeaderView(0)
        val usernameTextView = headerView.findViewById<TextView>(R.id.textViewLogin)
        val emailTextView = headerView.findViewById<TextView>(R.id.textViewEmail)

        usernameTextView.text = "Логин: $login"
        emailTextView.text = "Почта: $email"
    }

    private fun logoutUser() {
        val sharedPreferences = context.getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        val intent = Intent(context, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
        (context as AppCompatActivity).finish()
    }
}