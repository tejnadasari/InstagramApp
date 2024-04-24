package com.example.instagramapplication

import com.example.instagramapplication.models.Product
import retrofit2.Call
import retrofit2.http.GET

interface FakeStoreApiService {
    @GET("products")
    fun getProducts(): Call<List<Product>>
}