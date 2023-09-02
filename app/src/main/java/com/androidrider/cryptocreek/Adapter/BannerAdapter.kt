package com.androidrider.cryptocreek.Adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.androidrider.cryptocreek.R


class BannerAdapter(private val imageResources: IntArray) :
    RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.banner_layout, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val imageResource = imageResources[position]

        // Load and display the drawable resource in the ImageView
        holder.bannerImage.setImageResource(imageResource)
    }

    override fun getItemCount(): Int {
        return imageResources.size
    }

    inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val bannerImage: ImageView = itemView.findViewById(R.id.bannerViewPager)
//        val bannerImage: ImageView = itemView
    }
}

