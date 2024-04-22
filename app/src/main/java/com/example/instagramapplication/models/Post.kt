package com.example.instagramapplication.models

data class Post(
    val userName: String,
    val location: String,
    val likes: Int,
    val imageUrl: String = ""
)
