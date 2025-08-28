package com.arshcreations.careerflow.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arshcreations.careerflow.databinding.ItemDashboardCardBinding

data class DashboardItem(val title: String, val imageUrl: String)

class DashboardAdapter(private val items: List<DashboardItem>) :
    RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder>() {

    inner class DashboardViewHolder(val binding: ItemDashboardCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val binding =
            ItemDashboardCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DashboardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val item = items[position]
        holder.binding.itemTitle.text = item.title
        // In a real app, you would use a library like Glide or Picasso to load the image
        // For now, we'll skip image loading.
        // Example: Glide.with(holder.itemView.context).load(item.imageUrl).into(holder.binding.itemImage)
    }

    override fun getItemCount() = items.size
}