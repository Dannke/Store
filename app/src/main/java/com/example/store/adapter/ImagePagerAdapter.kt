package com.example.store.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.store.R

class ImagePagerAdapter(private val images: List<Int>) : RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

    // Добавляем два дополнительных элемента для бесконечного скролла
    private val infiniteImages by lazy {
        when {
            images.size > 1 -> {
                val last = images.last()
                val first = images.first()
                listOf(last) + images + listOf(first)
            }
            else -> images
        }
    }


    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.imageView.setImageResource(infiniteImages[position])
    }

    override fun getItemCount(): Int {
        return infiniteImages.size
    }
}