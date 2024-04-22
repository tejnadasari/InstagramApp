package com.example.instagramapplication.models

data class User(
    val userId: String = "",
    val name: String = "",
    val emailId: String = "",
    val username: String = "",
    val bio: String = "sampleBioForUser",
    val profilePictureUrl: String = "",
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val posts: List<Post> = listOf()
)