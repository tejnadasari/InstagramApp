package com.example.instagramapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramapplication.adapters.PostAdapter
import com.example.instagramapplication.models.Post

class UserHomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private var posts: List<Post> = listOf(
        Post("Alice", "New York", 150, "https://example.com/image1.jpg"),
        Post("Bob", "San Francisco", 95, "https://example.com/image2.jpg"),
        Post("Charlie", "London", 200, "https://example.com/image3.jpg")
    ) // Example posts

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_home)

        setupRecyclerView()
        setupNavigationBar()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.rvPosts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        postAdapter = PostAdapter(posts)
        recyclerView.adapter = postAdapter
    }

    private fun setupNavigationBar() {
        val btnFeed = findViewById<Button>(R.id.btnFeed)
        val btnShop = findViewById<Button>(R.id.btnShop)
        val btnProfile = findViewById<Button>(R.id.btnProfile)

        btnFeed.setOnClickListener {
            Toast.makeText(this, "Feed Clicked!", Toast.LENGTH_SHORT).show()
            recreate()
        }

        btnShop.setOnClickListener {
            Toast.makeText(this, "Shop Clicked!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ShopActivity::class.java)
            startActivity(intent)
        }

        btnProfile.setOnClickListener {
            Toast.makeText(this, "Profile Clicked!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
