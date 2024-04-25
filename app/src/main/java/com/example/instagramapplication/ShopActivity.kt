package com.example.instagramapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramapplication.models.Product
import com.example.instagramapplication.adapters.ProductAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShopActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private var products = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shop_activity)

        recyclerView = findViewById(R.id.shopRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ProductAdapter(products)

        fetchProducts()
        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        findViewById<Button>(R.id.btnFeed).setOnClickListener {
            onFeedClick()
        }
        findViewById<Button>(R.id.btnShop).setOnClickListener {
            onShopClick()
        }
        findViewById<Button>(R.id.btnProfile).setOnClickListener {
            onProfileClick()
        }
    }

    private fun fetchProducts() {
        RetrofitClient.retrofitInstance.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    response.body()?.let { productList ->
                        products.clear()
                        products.addAll(productList)
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
            }
        })
    }

    private fun onFeedClick() {
        val intent = Intent(this, UserHomeActivity::class.java)
        startActivity(intent)
    }

    private fun onShopClick() {
        val intent = Intent(this, ShopActivity::class.java)
        startActivity(intent)
    }

    private fun onProfileClick() {
        val intent = Intent(this, UserProfileActivity::class.java)
        startActivity(intent)
        finish()
    }
}
