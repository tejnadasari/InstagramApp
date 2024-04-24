package com.example.instagramapplication.models

data class Product(
    val id: Int = 0,
    val title: String = "",
    val price: Double = 0.00,
    val description: String = "",
    val image: String = "",
    val category: String = ""
)
