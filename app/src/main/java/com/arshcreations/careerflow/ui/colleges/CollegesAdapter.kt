package com.arshcreations.careerflow.ui.colleges

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arshcreations.careerflow.data.models.College
import com.arshcreations.careerflow.databinding.ItemCollegeCompactBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class CollegesAdapter(
    private val onCollegeClick: (College) -> Unit
) : ListAdapter<College, CollegesAdapter.CollegeViewHolder>(DiffCallback) {

    private val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val outputFormat = SimpleDateFormat("MMM dd", Locale.US)

    inner class CollegeViewHolder(val binding: ItemCollegeCompactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCollegeClick(getItem(position))
                }
            }
            binding.viewDetailsButton.setOnClickListener {
                binding.root.performClick()
            }
        }

        fun bind(college: College) {
            val context = itemView.context
            with(binding) {
                collegeName.text = college.name
                collegeLocation.text = formatLocation(college.city, college.state)
                collegeStream.text = college.stream
                collegeFees.text = formatFees(college.fees)
                avgPackage.text = formatPackage(college.avgPackage)
                setupRating(college.nirfRanking, ratingChip)
                loadBackgroundImage(context, college.images, collegeBackgroundImage)
                collegeExams.text = college.requiredExams.joinToString(", ").ifEmpty { "N/A" }
                admissionDate.text = formatAdmissionDate(college.admissionStart)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollegeViewHolder {
        val binding = ItemCollegeCompactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CollegeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CollegeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<College>() {
        override fun areItemsTheSame(oldItem: College, newItem: College): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: College, newItem: College): Boolean {
            return oldItem == newItem
        }
    }

    private fun formatLocation(city: String?, state: String?): String {
        return when {
            !city.isNullOrBlank() && !state.isNullOrBlank() -> "$city, $state"
            !city.isNullOrBlank() -> city
            !state.isNullOrBlank() -> state
            else -> "Location not available"
        }
    }

    private fun formatFees(fees: Double): String {
        return when {
            fees <= 0.0 -> "Fees: N/A"
            else -> String.format(Locale.US, "%.1fL per year", fees / 100_000)
        }
    }

    private fun formatPackage(avgPackage: Double): String {
        return when {
            avgPackage <= 0 -> "Avg Pkg: N/A"
            else -> String.format(Locale.US, "Avg Pkg: %.1f LPA", avgPackage / 100_000)
        }
    }

    private fun setupRating(
        nirfRanking: Long,
        ratingChip: com.google.android.material.chip.Chip
    ) {
        if (nirfRanking > 0) {
            // This is a simple placeholder for rating logic.
            // You can implement a more sophisticated conversion from NIRF rank to a 5-star rating.
            val rating = 5.0 - (nirfRanking / 50.0).coerceAtMost(4.5)
            ratingChip.text = String.format(Locale.US, "%.1f", rating)
            ratingChip.visibility = View.VISIBLE
        } else {
            ratingChip.visibility = View.GONE
        }
    }

    private fun loadBackgroundImage(context: Context, images: List<String>, imageView: ImageView) {
        val url = images.firstOrNull()
        if (!url.isNullOrEmpty()) {
            Glide.with(context)
                .load(url)
                .transform(CenterCrop(), RoundedCorners(32)) // Increased corner radius for better look
                .into(imageView)
        } else {
            // You might want to set a placeholder color or image here
            imageView.setImageDrawable(null)
        }
    }

    private fun formatAdmissionDate(dateString: String?): String {
        if (dateString.isNullOrBlank()) {
            return "Starts: N/A"
        }
        return try {
            val date = inputFormat.parse(dateString)
            "Starts: ${outputFormat.format(date!!)}"
        } catch (e: Exception) {
            "Starts: N/A"
        }
    }
}