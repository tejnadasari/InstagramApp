package com.example.instagramapplication.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.example.instagramapplication.models.Post
import com.squareup.picasso.Picasso

class GridImageAdapter(private val context: Context, private val posts: List<Post>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView: ImageView
        if (convertView == null) {
            imageView = ImageView(context)
            imageView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            imageView = convertView as ImageView
        }

        // Load image using Picasso or Glide
        Picasso.get().load(posts[position].imageUrl).into(imageView)

        return imageView
    }

    override fun getItem(position: Int): Any = posts[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = posts.size
}
