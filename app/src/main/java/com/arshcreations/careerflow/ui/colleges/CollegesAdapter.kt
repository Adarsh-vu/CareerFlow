package com.arshcreations.careerflow.ui.colleges

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arshcreations.careerflow.databinding.ItemCollegeCompactBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class CollegesAdapter(
    private val onCollegeClick: ((College) -> Unit)? = null
) : ListAdapter<College, CollegesAdapter.CollegeViewHolder>(DiffCallback) {

    private val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val outputFormat = SimpleDateFormat("MMM dd", Locale.US)

    inner class CollegeViewHolder(val binding: ItemCollegeCompactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onCollegeClick?.invoke(getItem(pos))
                }
            }
            binding.viewDetailsButton.setOnClickListener {
                binding.root.performClick()
            }
        }

        fun bind(college: College) {
            val context = itemView.context
            with(binding) {
                collegeName.text = college.name ?: "Unknown College"
                collegeLocation.text = formatLocation(college.city, college.state)
                collegeStream.text = college.stream ?: "Not Specified"
                collegeFees.text = formatFees(college.fees)
                avgPackage.text = formatPackage(college.avgPackage)
                setupRating(college.nirfRanking, ratingChip)
                loadBackgroundImage(context, college.images, collegeBackgroundImage)

                if (college.requiredExams.isNullOrEmpty()) {
                    collegeExams.text = "Exams: N/A"
                } else {
                    collegeExams.text = college.requiredExams?.joinToString(", ")
                }

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
        val college = getItem(position)
        holder.bind(college)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<College>() {
        override fun areItemsTheSame(oldItem: College, newItem: College): Boolean {
            // Using a unique ID is best, but name can work if it's unique.
            return oldItem.name == newItem.name
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

    private fun formatFees(fees: Double?): String {
        return when {
            fees == null || fees <= 0.0 -> "Fees: N/A"
            else -> String.format(Locale.US, "%.1fL per year", fees / 100_000)
        }
    }

    private fun formatPackage(avgPackage: Double?): String {
        return when {
            avgPackage == null || avgPackage <= 0 -> "Avg Pkg: N/A"
            else -> String.format(Locale.US, "Avg Pkg: %.1f LPA", avgPackage / 100_000)
        }
    }

    private fun setupRating(
        nirfRanking: Long?,
        ratingChip: com.google.android.material.chip.Chip
    ) {
        when {
            nirfRanking != null && nirfRanking in 1..100 -> {
                // Rank 1 -> 5.0 ; Rank 100 -> 1.0
                val rating = 5.0 - ((nirfRanking - 1) * 4.0 / 99.0)
                ratingChip.text = String.format(Locale.US, "%.1f", rating)
                ratingChip.visibility = View.VISIBLE
            }
            nirfRanking != null && nirfRanking > 100 -> {
                val rating = String.format(Locale.US, "%.1f", nirfRanking / 20.0)
                ratingChip.text = rating
                ratingChip.visibility = View.VISIBLE
            }
            else -> ratingChip.visibility = View.GONE
        }
    }

    private fun loadBackgroundImage(context: Context, images: List<String>?, imageView: ImageView) {
        val url = images?.firstOrNull()
        if (!url.isNullOrEmpty()) {
            Glide.with(context)
                .load(url)
                .transform(CenterCrop(), RoundedCorners(12))
                .into(imageView)
        } else {
            imageView.setImageResource(android.R.color.transparent)
        }
    }

    private fun formatAdmissionDate(dateString: String?): String {
        if (dateString.isNullOrBlank()) {
            return "Starts: N/A"
        }
        return try {
            val date = inputFormat.parse(dateString)
            "Starts: ${outputFormat.format(date)}"
        } catch (e: Exception) {
            "Starts: N/A"
        }
    }
}