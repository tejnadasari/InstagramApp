package com.example.instagramapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class SignInPageActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin_page)
        Toast.makeText(this, "Activity initialized", Toast.LENGTH_SHORT).show()

        auth = FirebaseAuth.getInstance()

        val emailEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val signInButton = findViewById<Button>(R.id.submit_button)
        val signUpLink = findViewById<TextView>(R.id.sign_up_link)

        signInButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password must not be empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Sign in button clicked", Toast.LENGTH_SHORT).show()
            signIn(email, password)
        }

        signUpLink.setOnClickListener {
            Toast.makeText(this, "Navigating to sign-up page", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SignUpPageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signIn(email: String, password: String) {
        Toast.makeText(this, "Attempting to sign in", Toast.LENGTH_SHORT).show()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Authentication succeeded", Toast.LENGTH_SHORT).show()
                    updateUI(auth.currentUser)
                } else {
                    Toast.makeText(this, "Authentication failed: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            storeLocalUserInfo(user)
            Toast.makeText(this, "Signed in as: ${user.email}", Toast.LENGTH_LONG).show()
            val intent = Intent(this, UserHomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val passwordEditText = findViewById<EditText>(R.id.password)
            passwordEditText.text.clear()
            passwordEditText.requestFocus()
            Toast.makeText(this, "Please check your credentials and try again.", Toast.LENGTH_LONG).show()
        }
    }

    private fun storeLocalUserInfo(user: FirebaseUser){
        val email = user.email ?: ""
        val username = email.substringBefore("@")
        val prefs = this.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("userId", user.uid)
            putString("username", username)
            apply()
        }
    }
}