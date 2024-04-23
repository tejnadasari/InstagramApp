//package com.example.instagramapplication
//
//import android.content.Intent
//import android.graphics.Bitmap
//import android.graphics.drawable.BitmapDrawable
//import android.os.Bundle
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.FileProvider
//import com.example.instagramapplication.models.Post
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.squareup.picasso.Picasso
//import java.io.File
//import java.io.FileOutputStream
//import java.io.IOException
//
//class UserPostDetailActivity : AppCompatActivity() {
//
//    private lateinit var post: Post
//    private lateinit var likesCountTextView: TextView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_user_post_detail)
//
//        // Retrieve the Post object passed with intent
//        val post = intent.getParcelableExtra<Post>("Post")
//
//        // Initialize views
//        val postImageView = findViewById<ImageView>(R.id.postImageView)
//        val userNameTextView = findViewById<TextView>(R.id.tvUserName)
//        val locationTextView = findViewById<TextView>(R.id.tvLocation)
//        val likesCountTextView = findViewById<TextView>(R.id.tvLikesCount)
//        val likeButtonImageView = findViewById<ImageView>(R.id.likeButton)
//        val shareButton = findViewById<ImageView>(R.id.shareButton)
//
//        // Populate the views with the Post data
//        post?.let {
//            // Picasso.get().load(it.imageUrl).into(postImageView)
//            Picasso.get().load(it.imageUrl).into(postImageView, object : com.squareup.picasso.Callback {
//                override fun onSuccess() {
//                    shareButton.setOnClickListener {
//                        val bitmap = (postImageView.drawable as BitmapDrawable).bitmap
//                        sharePost(bitmap)
//                    }
//                }
//
//                override fun onError(e: Exception?) {
//                    Toast.makeText(applicationContext, "Failed to load image: ${e?.message}", Toast.LENGTH_LONG).show()
//                }
//            })
//            userNameTextView.text = it.userName
//            locationTextView.text = it.location
//            likesCountTextView.text = "${it.likes} likes"
//
//            // TODO: Set onClickListener for the like button
//            likeButtonImageView.setOnClickListener {
//                incrementLikeCount()
//            }
//        }
//    }
//
//    private fun incrementLikeCount() {
//        // Get the current user's ID
//        val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
//        // Reference to the post's document
//        val postRef = FirebaseFirestore.getInstance()
//            .collection("users")
//            .document(userId)
//            .collection("posts")
//            .document(post.postId)
//
//        // Increment likes in Firestore transaction
//        FirebaseFirestore.getInstance().runTransaction { transaction ->
//            val snapshot = transaction.get(postRef)
//            val newLikeCount = snapshot.getLong("likes")?.plus(1) ?: 1L // Start at 1 if null
//            transaction.update(postRef, "likes", newLikeCount)
//
//            // Update the local post object and UI
//            post.likes = newLikeCount.toInt()
//            // Note: UI updates must be run on the UI thread
//            runOnUiThread {
//                findViewById<TextView>(R.id.tvLikesCount).text = "${post.likes} likes"
//            }
//
//            // Success
//            null
//        }.addOnSuccessListener {
//            Toast.makeText(this, "Liked!", Toast.LENGTH_SHORT).show()
//        }.addOnFailureListener { e ->
//            Toast.makeText(this, "Failed to like: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
//        }
//    }
//    // You may want to create a method to update the likes on the UI and Firestore
//    private fun updateLikes(post: Post) {
//        // Code to update the like count in Firestore
//        // Update the likesCountTextView to reflect new likes
//    }
//
////    private fun sharePost(image: Bitmap) {
////        try {
////            // Save the image to the external cache directory for sharing
////            val imageFile = File(requireActivity().externalCacheDir, "share_image.jpg")
////            val fos = FileOutputStream(imageFile)
////            image.compress(Bitmap.CompressFormat.JPEG, 100, fos)
////            fos.flush()
////            fos.close()
////
////            val imageUri = FileProvider.getUriForFile(
////                requireContext(),
////                "edu.utap.fragment.provider",
////                imageFile
////            )
////            val shareIntent = Intent(Intent.ACTION_SEND)
////            shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
////            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
////            shareIntent.type = "image/jpeg"
////            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
////            startActivity(Intent.createChooser(shareIntent, "Share Image"))
////        } catch (e: IOException) {
////            e.printStackTrace()
////            Toast.makeText(context, "Failed to share image: ${e.message}", Toast.LENGTH_LONG).show()
////        }
////    }
//private fun sharePost(image: Bitmap?) {
//    image?.let {
//        try {
//            val imageFile = File(externalCacheDir, "share_image.jpg")
//            val fos = FileOutputStream(imageFile)
//            it.compress(Bitmap.CompressFormat.JPEG, 100, fos)
//            fos.flush()
//            fos.close()
//
//            val imageUri = FileProvider.getUriForFile(
//                this,
//                "com.example.instagramapplication.fileprovider",
//                imageFile
//            )
//
//            val shareIntent = Intent(Intent.ACTION_SEND)
//            shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
//            shareIntent.type = "image/jpeg"
//            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            startActivity(Intent.createChooser(shareIntent, "Share Image"))
//        } catch (e: IOException) {
//            e.printStackTrace()
//            Toast.makeText(this, "Failed to share image: ${e.message}", Toast.LENGTH_LONG).show()
//        }
//    }
//}
//
//}
