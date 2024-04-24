package com.example.instagramapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramapplication.R
import com.example.instagramapplication.models.Product
import com.squareup.picasso.Picasso

class ProductAdapter(private val products: List<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
    }

    override fun getItemCount() = products.size

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView: TextView = itemView.findViewById<TextView>(R.id.productTitle)
        private val imageView: ImageView = itemView.findViewById<ImageView>(R.id.productImage)
        private val priceView: TextView = itemView.findViewById<TextView>(R.id.productPrice)

        fun bind(product: Product) {
            titleView.text = product.title
            priceView.text = "$${product.price}"
            Picasso.get().load(product.image).into(imageView)
        }
    }
}
