package com.example.instagramapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.Manifest
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.content.Context
import android.graphics.Rect
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramapplication.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.util.UUID

class UserProfileActivity : AppCompatActivity() {

    private lateinit var cameraResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryResultLauncher: ActivityResultLauncher<Intent>

    private lateinit var postsRecyclerView: RecyclerView
    private lateinit var gridAdapter: GridAdapter
    private var imageUrls = mutableListOf<String>()

    private lateinit var btnFollow: Button
    private var isFollowing: Boolean = false
    private var profileUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile)


        val username = intent.getStringExtra("Username")
        val currentUserUsername = getUsernameFromSharedPreferences()
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if (username != null && username != currentUserUsername) {
            loadOtherUserProfile(username) { userId ->
                this.profileUserId = userId
                checkFollowState()

                btnFollow.setOnClickListener {
                    if (isFollowing) {
                        unfollowUser(FirebaseFirestore.getInstance(), currentUserId.orEmpty(), userId)
                    } else {
                        followUser(FirebaseFirestore.getInstance(), currentUserId.orEmpty(), userId)
                    }
                }
                loadProfileData(this.profileUserId.orEmpty())
            }
            findViewById<Button>(R.id.btnAddPost).visibility = View.GONE
        } else {
            loadProfileData(currentUserId.toString())
            findViewById<Button>(R.id.btnFollow).visibility = View.GONE
            loadProfile()
        }

        postsRecyclerView = findViewById(R.id.postsRecyclerView)

        val spacingInPixels = 2
        postsRecyclerView.addItemDecoration(GridSpacingItemDecoration(3, spacingInPixels))


        gridAdapter = GridAdapter(imageUrls)
        postsRecyclerView.adapter = gridAdapter
        postsRecyclerView.layoutManager = GridLayoutManager(this, 3)

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
        loadUserImages()
        btnFollow = findViewById<Button>(R.id.btnFollow)
        btnFollow.setOnClickListener {
            onFollowButtonClick()
        }
    }

    private fun updateCounts(followersCount: Int, followingCount: Int) {
        findViewById<TextView>(R.id.followerCount).text = "Followers: $followersCount"
        findViewById<TextView>(R.id.followingCount).text = "Following: $followingCount"
    }

    private fun loadProfileData(userId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                val followersCount = user?.followersCount ?: 0
                val followingCount = user?.followingCount ?: 0
                updateCounts(followersCount, followingCount)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading profile data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadUserImages() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        FirebaseFirestore.getInstance().collection("users").document(userId).collection("posts")
            .get()
            .addOnSuccessListener { documents ->
                val imageUrls = documents.mapNotNull { it.getString("imageUrl") }
                gridAdapter.updateData(imageUrls)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading images: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupButtonListeners() {
        findViewById<Button>(R.id.btnMessage).setOnClickListener {
            onMessageClick()
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
    }

    private fun loadOtherUserProfile(username: String, onProfileLoaded: (String) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").whereEqualTo("username", username).limit(1).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    onProfileLoaded(document.id)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error loading user profile: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun onMessageClick() {
        val intent = Intent(this, ChatActivity::class.java)
        startActivity(intent)
    }

    private fun onSettingsClick() {

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

    private fun onAddPostClick() {
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

    private fun getUsernameFromSharedPreferences(): String {
        val prefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return prefs.getString("username", "Anonymous") ?: "Anonymous"
    }

    private fun savePostToFirestore(userId: String, imageUrl: String) {
        val username = getUsernameFromSharedPreferences()
        val db = FirebaseFirestore.getInstance()
        val userPostsRef = db.collection("users").document(userId).collection("posts")

        val newPost = hashMapOf(
            "imageUrl" to imageUrl,
            "location" to "Unknown Location",
            "likes" to 0,
            "userName" to username
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

    private fun checkFollowState() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        profileUserId?.let { userId ->
            db.collection("follows")
                .whereEqualTo("followerUserId", currentUserId)
                .whereEqualTo("followingUserId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    isFollowing = !documents.isEmpty
                    updateFollowButton()
                }
        }
    }

    private fun updateFollowButton() {
        btnFollow.text = if (isFollowing) "Following" else "Follow"
    }

    private fun onFollowButtonClick() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        profileUserId?.let { userId ->
            val db = FirebaseFirestore.getInstance()
            if (isFollowing) {
                unfollowUser(db, currentUserId, userId)
            } else {
                followUser(db, currentUserId, userId)
            }
        }
    }

    private fun followUser(db: FirebaseFirestore, currentUserId: String, userIdToFollow: String) {
        val batch = db.batch()

        val followData = hashMapOf(
            "followerUserId" to currentUserId,
            "followingUserId" to userIdToFollow
        )
        val followRef = db.collection("follows").document()
        batch.set(followRef, followData)

        val currentUserRef = db.collection("users").document(currentUserId)
        batch.update(currentUserRef, "followingCount", FieldValue.increment(1))

        val profileUserRef = db.collection("users").document(userIdToFollow)
        batch.update(profileUserRef, "followersCount", FieldValue.increment(1))

        batch.commit().addOnCompleteListener {
            if (it.isSuccessful) {
                isFollowing = true
                updateFollowButton()
            } else {
                Toast.makeText(this, "Failed to follow user", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun unfollowUser(db: FirebaseFirestore, currentUserId: String, userIdToUnfollow: String) {
        val followsRef = db.collection("follows")
        val query = followsRef.whereEqualTo("followerUserId", currentUserId).whereEqualTo("followingUserId", userIdToUnfollow)

        query.get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val batch = db.batch()

                for (document in documents) {
                    batch.delete(followsRef.document(document.id))
                }

                val currentUserRef = db.collection("users").document(currentUserId)
                batch.update(currentUserRef, "followingCount", FieldValue.increment(-1))

                val profileUserRef = db.collection("users").document(userIdToUnfollow)
                batch.update(profileUserRef, "followersCount", FieldValue.increment(-1))

                batch.commit().addOnCompleteListener {
                    if (it.isSuccessful) {
                        isFollowing = false
                        updateFollowButton()
                    } else {
                        Toast.makeText(this, "Failed to unfollow user", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error unfollowing user: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }


    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_PICK = 2
        private const val REQUEST_CAMERA_PERMISSION = 1001
    }

}

class GridAdapter(private var items: List<String>) : RecyclerView.Adapter<GridAdapter.ViewHolder>() {

    fun updateData(newItems: List<String>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_image_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.get().load(items[position]).into(holder.imageView)
    }

    override fun getItemCount() = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView) // This is the id from your grid_image_item.xml
    }


}

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        if (position >= spanCount) {
            outRect.top = spacing
        }
        if (column < spanCount - 1) {
            outRect.right = spacing
        }
    }
}



