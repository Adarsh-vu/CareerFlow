package com.arshcreations.careerflow.ui.colleges

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arshcreations.careerflow.R
import com.arshcreations.careerflow.databinding.ItemCollegeBinding
import com.bumptech.glide.Glide
import java.util.Locale

class CollegesAdapter(private val colleges: List<College>) :
    RecyclerView.Adapter<CollegesAdapter.CollegeViewHolder>() {

    inner class CollegeViewHolder(val binding: ItemCollegeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollegeViewHolder {
        val binding =
            ItemCollegeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollegeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CollegeViewHolder, position: Int) {
        val college = colleges[position]
        val context = holder.itemView.context

        holder.binding.collegeName.text = college.name

        holder.binding.collegeLocation.text = context.getString(R.string.college_location_format, college.city, college.state)

        holder.binding.collegeStream.text = college.stream
        holder.binding.collegeFees.text = context.getString(R.string.college_fees_format, college.fees ?: 0)

        // Safely handle rating
        if (college.nirfRanking != null && college.nirfRanking > 0) {
            val rating = String.format(Locale.US, "%.1f", college.nirfRanking / 20.0)
            holder.binding.ratingChip.text = rating
            holder.binding.ratingChip.visibility = View.VISIBLE
        } else {
            holder.binding.ratingChip.visibility = View.GONE
        }

        // Safely load the college image
        if (!college.images.isNullOrEmpty()) {
            Glide.with(context)
                .load(college.images[0])
                .into(holder.binding.collegeImage)
        }
    }

    override fun getItemCount() = colleges.size
}
