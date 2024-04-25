package com.example.instagramapplication.models

data class Message(
    var text: String? = null,
    var author: String? = null,
    var timestamp: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now()
)