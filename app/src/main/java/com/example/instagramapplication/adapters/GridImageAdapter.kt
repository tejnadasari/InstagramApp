package com.example.instagramapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramapplication.R
import com.example.instagramapplication.models.Post
import com.squareup.picasso.Picasso

// GridAdapter.kt
class GridAdapter(private val imageUrls: MutableList<String>) :
    RecyclerView.Adapter<GridAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.grid_image_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Picasso.get()
            .load(imageUrls[position])
            .placeholder(R.drawable.image_placeholder) // optional
            .error(R.drawable.image_error) // optional
            .into(holder.imageView)
    }

    override fun getItemCount() = imageUrls.size

    fun updateData(newImageUrls: List<String>) {
        imageUrls.clear()
        imageUrls.addAll(newImageUrls)
        notifyDataSetChanged()
    }

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }
}

