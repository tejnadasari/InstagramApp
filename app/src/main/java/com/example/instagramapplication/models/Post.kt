package com.example.instagramapplication.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    var postId: String = "",
    val userName: String = "",
    val location: String = "",
    var likes: Int = 0,
    val imageUrl: String = ""
) : Parcelable
