package com.example.instagramapplication

import MessageAdapter
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramapplication.models.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatActivity : AppCompatActivity() {

    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var messagesRecyclerView: RecyclerView
    private val messageAdapter = MessageAdapter()
    private val db = FirebaseFirestore.getInstance()
    private val messageCollection = db.collection("messages")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        messagesRecyclerView = findViewById(R.id.messageRecyclerView)
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)

        messagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = messageAdapter
        }

        messageCollection.orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Listen failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val messages = snapshots?.documents?.mapNotNull {
                    it.toObject(Message::class.java)
                }
                messages?.let { messageAdapter.submitList(it) }
            }

        sendButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun sendMessage() {
        val messageText = messageEditText.text.toString()
        if (messageText.isNotEmpty()) {
            val username = getUsernameFromSharedPreferences()
            val message = Message(text = messageText, author = username)
            messageCollection.add(message)
            messageEditText.text.clear()
        }
    }

    private fun getUsernameFromSharedPreferences(): String {
        val prefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return prefs.getString("username", "Anonymous") ?: "Anonymous"
    }
}
