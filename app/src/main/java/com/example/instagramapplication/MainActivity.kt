package com.example.instagramapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin_page)
        Toast.makeText(this, "Welcome to the Sign In Page", Toast.LENGTH_LONG).show()

        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById<EditText>(R.id.username)
        passwordEditText = findViewById<EditText>(R.id.password)
        val signInButton = findViewById<Button>(R.id.submit_button)
        val signUpLink = findViewById<TextView>(R.id.sign_up_link)

        signInButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password must not be empty", Toast.LENGTH_LONG).show()
            } else {
                signIn(email, password)
            }
        }

        signUpLink.setOnClickListener {
            startActivity(Intent(this, SignUpPageActivity::class.java))
        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    updateUI(auth.currentUser)
                } else {
                    Toast.makeText(this, "Authentication failed: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            Toast.makeText(this, "Signed in as: ${user.email}", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, UserHomeActivity::class.java))
            finish()
        } else {
            // Use the member variable here
            passwordEditText.text.clear()
            passwordEditText.requestFocus()
            Toast.makeText(this, "Please check your credentials and try again.", Toast.LENGTH_LONG).show()
        }
    }
}
