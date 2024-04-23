package com.example.instagramapplication.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramapplication.R
//import com.example.instagramapplication.UserPostDetailActivity
import com.example.instagramapplication.models.Post
import com.squareup.picasso.Picasso

class PostAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUserName: TextView = view.findViewById(R.id.tvUserName)
        val tvLocation: TextView = view.findViewById(R.id.tvLocation)
        val imgPostImage: ImageView = view.findViewById(R.id.imgPostImage)
        val icHeart: ImageView = view.findViewById(R.id.icHeart)
        val tvLikeCount: TextView = view.findViewById(R.id.tvLikeCount)
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
//        Picasso.get().load(post.imageUrl).into(holder.imgPostImage)
//
//        holder.itemView.setOnClickListener {
//            val context = it.context
//            val intent = Intent(context, UserPostDetailActivity::class.java).apply {
//                putExtra("Post", post)
//            }
//            context.startActivity(intent)
//
//        }
    }

    override fun getItemCount() = posts.size
}
