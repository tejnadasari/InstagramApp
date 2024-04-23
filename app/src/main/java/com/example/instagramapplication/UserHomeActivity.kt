package com.example.instagramapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramapplication.adapters.PostAdapter
import com.example.instagramapplication.models.Post
import com.google.firebase.firestore.FirebaseFirestore

class UserHomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    //    private var posts: List<Post> = listOf(
//        Post("12", "Alice", "New York", 150, "https://example.com/image1.jpg"),
//        Post("123", "Bob", "San Francisco", 95, "https://example.com/image2.jpg"),
//        Post("1234", "Charlie", "London", 200, "https://example.com/image3.jpg")
//    ) // Example posts
    val allPosts = mutableListOf<Post>()
    val posts = mutableListOf<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_home)

        getAllPosts()
        setupRecyclerView()
        setupNavigationBar()
    }

    fun getAllPosts() {
        val usersCollection = FirebaseFirestore.getInstance().collection("users")

        usersCollection.get().addOnSuccessListener { userDocuments ->
            for (userDocument in userDocuments) {
                val postsCollection = usersCollection.document(userDocument.id).collection("posts")
                postsCollection.get().addOnSuccessListener { postDocuments ->
                    for (postDocument in postDocuments) {
                        val post = postDocument.toObject(Post::class.java).apply {
                            postId = postDocument.id // Ensure you have postId as part of your Post data class
                        }
                        allPosts.add(post)
                        posts.add(post)
                    }
                }.addOnFailureListener { e ->
                    Log.e("getAllPosts", "Error getting posts for user ${userDocument.id}", e)
                }
            }
        }.addOnFailureListener { e ->
            Log.e("getAllPosts", "Error getting users", e)
        }
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
