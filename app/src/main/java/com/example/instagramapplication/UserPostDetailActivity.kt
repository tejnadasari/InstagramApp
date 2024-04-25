package com.example.instagramapplication

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.instagramapplication.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class UserPostDetailActivity : AppCompatActivity() {

    private lateinit var post: Post
    private lateinit var likesCountTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_post_detail)

        post = intent.getParcelableExtra<Post>("Post") ?: return

        val postImageView = findViewById<ImageView>(R.id.postImageView)
        val userNameTextView = findViewById<TextView>(R.id.tvUserName)
        val locationTextView = findViewById<TextView>(R.id.tvLocation)
        val likesCountTextView = findViewById<TextView>(R.id.tvLikesCount)
        val likeButtonImageView = findViewById<ImageView>(R.id.likeButton)
        val shareButton = findViewById<Button>(R.id.shareButton)

        post?.let {
            Picasso.get().load(it.imageUrl).into(postImageView, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    shareButton.setOnClickListener {
                        val bitmap = (postImageView.drawable as BitmapDrawable).bitmap
                        sharePost(bitmap)
                    }
                }

                override fun onError(e: Exception?) {
                    Toast.makeText(applicationContext, "Failed to load image: ${e?.message}", Toast.LENGTH_LONG).show()
                }
            })
            userNameTextView.text = it.userName
            locationTextView.text = it.location
            likesCountTextView.text = "${it.likes} likes"

            likeButtonImageView.setOnClickListener {
                incrementLikeCount()
            }
        }

        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        findViewById<Button>(R.id.btnFeed).setOnClickListener {
            onFeedClick()
        }
        findViewById<Button>(R.id.btnShop).setOnClickListener {
            onShopClick()
        }
        findViewById<Button>(R.id.btnProfile).setOnClickListener {
            onProfileClick()
        }
    }

    private fun onFeedClick() {
        val intent = Intent(this, UserHomeActivity::class.java)
        startActivity(intent)
    }

    private fun onShopClick() {
        val intent = Intent(this, ShopActivity::class.java)
        startActivity(intent)
    }

    private fun onProfileClick() {
        val intent = Intent(this, UserProfileActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun incrementLikeCount() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val postRef = FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("posts")
            .document(post.postId)

        FirebaseFirestore.getInstance().runTransaction { transaction ->
            val snapshot = transaction.get(postRef)
            val newLikeCount = snapshot.getLong("likes")?.plus(1) ?: 1L // Start at 1 if null
            transaction.update(postRef, "likes", newLikeCount)

            post.likes = newLikeCount.toInt()
            runOnUiThread {
                findViewById<TextView>(R.id.tvLikesCount).text = "${post.likes} likes"
            }
            null
        }.addOnSuccessListener {
            Toast.makeText(this, "Liked!", Toast.LENGTH_SHORT).show()

            val data = Intent().apply {
                putExtra("UpdatedPost", post)
            }
            setResult(RESULT_OK, data)
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed to like: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

private fun sharePost(image: Bitmap?) {
    image?.let {
        try {
            val imageFile = File(externalCacheDir, "share_image.jpg")
            val fos = FileOutputStream(imageFile)
            it.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()

            val imageUri = FileProvider.getUriForFile(
                this,
                "com.example.instagramapplication.provider",
                imageFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
            shareIntent.type = "image/jpeg"
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(shareIntent, "Share Image"))
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to share image: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

}
