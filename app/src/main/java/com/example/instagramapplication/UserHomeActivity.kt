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
    var posts = mutableListOf<Post>()

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
            var totalPostsToFetch = 0
            var fetchedPostsCount = 0

            if (userDocuments.isEmpty) {
                postAdapter.notifyDataSetChanged()
            }

            for (userDocument in userDocuments) {
                val postsCollection = usersCollection.document(userDocument.id).collection("posts")
                postsCollection.get().addOnSuccessListener { postDocuments ->
                    totalPostsToFetch += postDocuments.size()
                    for (postDocument in postDocuments) {
                        var post = postDocument.toObject(Post::class.java).apply {
                            postId = postDocument.id
                        }
                        posts.add(post)
                        fetchedPostsCount++
                        if (fetchedPostsCount == totalPostsToFetch) {
                            postAdapter.notifyDataSetChanged()
                        }
                    }
                }.addOnFailureListener { e ->
                    Log.e("getAllPosts", "Error getting posts for user ${userDocument.id}: ${e.toString()}")
                }
            }
        }.addOnFailureListener { e ->
            Log.e("getAllPosts", "Error getting users: ${e.toString()}")
        }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.rvPosts)
        recyclerView.layoutManager = LinearLayoutManager(this)
//        postAdapter = PostAdapter(posts)
        postAdapter = PostAdapter(posts) { post ->
            val intent = Intent(this, UserPostDetailActivity::class.java)
            // Assuming Post class is Parcelable or Serializable
            intent.putExtra("Post", post)
            startActivity(intent)
        }
        recyclerView.adapter = postAdapter
        postAdapter.notifyDataSetChanged()
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
