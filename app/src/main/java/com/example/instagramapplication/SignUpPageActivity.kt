package com.example.instagramapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.instagramapplication.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore


class SignUpPageActivity: AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "SignInPageActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_page)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Get references to the EditTexts and Button
        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val signUpButton = findViewById<Button>(R.id.enter_button)

        // Set up the button click listener
        signUpButton.setOnClickListener {
            // Get text from EditTexts and convert them to strings
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Basic validation
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Create a new user with Firebase Auth
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign up success, update UI with the signed-in user's information
                            val user = auth.currentUser
                            // Optionally, you can add additional user information here (such as first and last names)
                            Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show()
                            if (user != null) {
                                createUserRecord(user, true)
                            }
                            updateUI(user)
                        } else {
                            // If sign up fails, display a message to the user.
                            Toast.makeText(this, "Sign up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            updateUI(null)
                        }
                    }
            } else {
                // Prompt user to enter all required fields
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // User is signed in successfully, navigate to the HomePageActivity
            val intent = Intent(this, UserHomeActivity::class.java)
            startActivity(intent)
            finish()  // Finish the current activity
        } else {
            // Clear the input fields if sign up failed
            findViewById<EditText>(R.id.password).text.clear()
        }
    }

    private fun createUserRecord(firebaseUser: FirebaseUser, isNewUser: Boolean) {
        val email = firebaseUser.email ?: ""
        val username = email.substringBefore("@")
        val name = firebaseUser.displayName ?: ""
        val firstName = name.substringBefore(" ")
        val lastName = name.substringAfter(" ", "")
        val posts: List<Post> = listOf()

        // Save user details in SharedPreferences
        val prefs = this.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("userId", firebaseUser.uid)
            putString("username", username)
            apply()
        }

        if (isNewUser) {
            // Create a new user with a first and last name
            val user = hashMapOf(
                "userId" to firebaseUser.uid,
                "name" to name,
                "emailid" to email,
                "username" to username,
                "bio" to "sampleBioForUser",
                "profilePictureUrl" to "",  // Blank or some default URL if you have one
                "followersCount" to 0,
                "followingCount" to 0,
                "posts" to posts
            )

            // Add a new document with a generated ID to Firestore
            db.collection("users").document(firebaseUser.uid).set(user)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }
    }

}