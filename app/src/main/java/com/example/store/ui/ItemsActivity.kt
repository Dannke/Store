package com.example.store.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.store.adapter.ItemsAdapter
import com.example.store.data.Item
import com.example.store.dataBase.DataBaseHelper
import com.example.store.databinding.ActivityItemsBinding

class ItemsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityItemsBinding
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var adapter: ItemsAdapter
    private var itemsList: List<Item> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DataBaseHelper(this, null)

        val navigationHelper = NavigationHelper(
            this,
            this,
            binding.drawerLayout,
            binding.toolbar,
            binding.navigationView
        )
        navigationHelper.setupNavigationView()

        // Загрузка всех товаров
        itemsList = dbHelper.getAllItems()

        // Настройка RecyclerView
        binding.itemsList.layoutManager = LinearLayoutManager(this)
        adapter = ItemsAdapter(itemsList, this)
        binding.itemsList.adapter = adapter

        // Обработчик поиска
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterItems()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Настройка категорий
        val categories = listOf("Бренд") + itemsList.map { it.category }.distinct()
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = categoryAdapter

        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterItems()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Опции сортировки по цене
        val priceSortOptions = listOf("Цена", "Цена: по возрастанию", "Цена: по убыванию")
        val priceSortAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priceSortOptions)
        priceSortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.priceSortSpinner.adapter = priceSortAdapter

        binding.priceSortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterItems()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun filterItems() {
        val searchText = binding.searchBar.text.toString()
        val selectedCategory = binding.categorySpinner.selectedItem.toString()
        val sortOption = binding.priceSortSpinner.selectedItemPosition

        val recyclerView = binding.itemsList
        val noItemsText = binding.noItemsText

        var filteredItems = itemsList.filter {
            it.title.contains(searchText, ignoreCase = true) &&
                    (selectedCategory == "Бренд" || it.category == selectedCategory)
        }

        filteredItems = when (sortOption) {
            1 -> filteredItems.sortedBy { it.price } // По возрастанию
            2 -> filteredItems.sortedByDescending { it.price } // По убыванию
            else -> filteredItems // Без сортировки
        }

        if (filteredItems.isEmpty()) {
            recyclerView.visibility = View.GONE
            noItemsText.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            noItemsText.visibility = View.GONE
            adapter.updateItems(filteredItems) // обновляем адаптер новыми товарами
        }

        adapter.items = filteredItems
        adapter.notifyDataSetChanged()
    }
}
