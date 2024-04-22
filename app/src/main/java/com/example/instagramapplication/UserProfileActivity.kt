package com.example.instagramapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.Manifest
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.UUID

class UserProfileActivity : AppCompatActivity() {

    private lateinit var cameraResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile)

        cameraResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                handleCameraResult(result.data)
            }
        }

        galleryResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                handleGalleryResult(result.data)
            }
        }

        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        findViewById<Button>(R.id.btnMessage).setOnClickListener {
            onMessageClick()
        }
        findViewById<Button>(R.id.btnSettings).setOnClickListener {
            onSettingsClick()
        }
        findViewById<Button>(R.id.btnFeed).setOnClickListener {
            onFeedClick()
        }
        findViewById<Button>(R.id.btnShop).setOnClickListener {
            onShopClick()
        }
        findViewById<Button>(R.id.btnProfile).setOnClickListener {
            onProfileClick()
        }
        findViewById<Button>(R.id.btnAddPost).setOnClickListener {
            onAddPostClick()
        }
    }

    private fun loadProfile() {
        // Code to load the user's profile data
    }

    private fun onMessageClick() {
        // Navigate to the message screen
//        val intent = Intent(this, MessageActivity::class.java)
//        startActivity(intent)
    }

    private fun onSettingsClick() {
        // Navigate to the settings screen
//        val intent = Intent(this, SettingsActivity::class.java)
//        startActivity(intent)
    }

    private fun onFeedClick() {
        // Navigate to the feed/home screen
        val intent = Intent(this, UserHomeActivity::class.java)
        startActivity(intent)
    }

    private fun onShopClick() {
        // Navigate to the shop screen
        // You need to create a ShopActivity and replace 'ShopActivity::class.java' with the actual class name
        val intent = Intent(this, ShopActivity::class.java)
        startActivity(intent)
    }

    private fun onProfileClick() {
        // Refresh the profile screen by restarting the UserProfileActivity
        val intent = Intent(this, UserProfileActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onAddPostClick() {
        // Show a dialog to ask the user if they want to use the camera or upload from gallery
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        AlertDialog.Builder(this).setItems(options) { dialog, which ->
            when (options[which]) {
                "Take Photo" -> openCamera()
                "Choose from Gallery" -> openGallery()
                "Cancel" -> dialog.dismiss()
            }
        }.show()
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        } else {
            launchCamera()
        }
    }

    private fun launchCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraResultLauncher.launch(takePictureIntent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openGallery() {
        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryResultLauncher.launch(pickPhotoIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> handleCameraResult(data)
                REQUEST_IMAGE_PICK -> handleGalleryResult(data)
            }
        }
    }

    private fun handleCameraResult(data: Intent?) {
        data?.extras?.get("data")?.let {
            uploadBitmapToFirebase(it as Bitmap)
        }
    }

    private fun handleGalleryResult(data: Intent?) {
        data?.data?.let {
            uploadUriToFirebase(it)
        }
    }

    private fun uploadBitmapToFirebase(bitmap: Bitmap) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val storageRef = FirebaseStorage.getInstance().reference.child("images/$userId/${UUID.randomUUID()}.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = storageRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            it.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                savePostToFirestore(userId, uri.toString())
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePostToFirestore(userId: String, imageUrl: String) {
        val db = FirebaseFirestore.getInstance()
        val userPostsRef = db.collection("users").document(userId).collection("posts")

        val newPost = hashMapOf(
            "imageUrl" to imageUrl,
            "location" to "Unknown Location",
            "likes" to 0,
            "userName" to "Username Placeholder"
        )

        userPostsRef.add(newPost).addOnSuccessListener {
            Toast.makeText(this, "Post added to Firestore", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to add post to Firestore: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }



    private fun uploadUriToFirebase(imageUri: Uri) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val storageRef = FirebaseStorage.getInstance().reference.child("images/$userId/${UUID.randomUUID()}.jpg")

        val uploadTask = storageRef.putFile(imageUri)
        uploadTask.addOnSuccessListener {
            it.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                savePostToFirestore(userId, uri.toString())
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_PICK = 2
        private const val REQUEST_CAMERA_PERMISSION = 1001
    }



}
