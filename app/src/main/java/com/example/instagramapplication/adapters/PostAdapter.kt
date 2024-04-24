package com.example.instagramapplication.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramapplication.R
import com.example.instagramapplication.UserPostDetailActivity
import com.example.instagramapplication.models.Post
import com.squareup.picasso.Picasso

class PostAdapter(private val posts: List<Post>, private val onPostClick: (Post) -> Unit, private val onUsernameClick: (String) -> Unit) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUserName: TextView = view.findViewById(R.id.tvUserName)
        val tvLocation: TextView = view.findViewById(R.id.tvLocation)
        val imgPostImage: ImageView = view.findViewById(R.id.imgPostImage)
        val icHeart: ImageView = view.findViewById(R.id.icHeart)
        val tvLikeCount: TextView = view.findViewById(R.id.tvLikeCount)

        init {
            imgPostImage.setOnClickListener {
                // Use adapterPosition to get the clicked item from the list
                val post = posts[adapterPosition]
                onPostClick(post)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.tvUserName.text = post.userName
        holder.tvLocation.text = post.location
        holder.tvLikeCount.text = "${post.likes} likes"
        Picasso.get().load(post.imageUrl).into(holder.imgPostImage)

        holder.imgPostImage.setOnClickListener {
            val context = it.context
            val intent = Intent(context, UserPostDetailActivity::class.java).apply {
                putExtra("Post", post)
            }
            context.startActivity(intent)
        }

        holder.tvUserName.setOnClickListener {
            onUsernameClick(post.userName)
        }
    }

    override fun getItemCount() = posts.size
}
