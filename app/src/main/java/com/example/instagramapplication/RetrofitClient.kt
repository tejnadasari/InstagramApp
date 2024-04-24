package com.example.instagramapplication

import com.example.instagramapplication.FakeStoreApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    private const val BASE_URL = "https://fakestoreapi.com/"

    val retrofitInstance: FakeStoreApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(FakeStoreApiService::class.java)
    }
}
