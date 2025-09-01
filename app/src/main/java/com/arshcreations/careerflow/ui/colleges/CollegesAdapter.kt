package com.arshcreations.careerflow.ui.colleges

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arshcreations.careerflow.databinding.ItemCollegeCompactBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.Locale

class CollegesAdapter(
    private val colleges: List<College>,
    private val onCollegeClick: ((College) -> Unit)? = null
) : RecyclerView.Adapter<CollegesAdapter.CollegeViewHolder>() {

    inner class CollegeViewHolder(val binding: ItemCollegeCompactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Click on whole item
            binding.root.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onCollegeClick?.invoke(colleges[pos])
                }
            }
            // Click on "View details"
            binding.viewDetailsButton.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onCollegeClick?.invoke(colleges[pos])
                }
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
        val college = colleges[position]
        val context = holder.itemView.context

        with(holder.binding) {
            // Name
            collegeName.text = college.name ?: "Unknown College"

            // Location
            collegeLocation.text = formatLocation(college.city, college.state)

            // Stream chip
            collegeStream.text = college.stream ?: "Not Specified"

            // Fees & Avg package
            collegeFees.text = formatFees(college.fees)
            avgPackage.text = formatPackage(college.avgPackage)

            // Rating chip from NIRF rank
            setupRating(college.nirfRanking, ratingChip)

            // Faded background image
            loadBackgroundImage(context, college.images, collegeBackgroundImage)
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
            fees == null || fees <= 0 -> "Fees not available"
            fees >= 100_000 -> String.format(Locale.US, "%.1fL per year", fees / 100_000)
            else -> String.format(Locale.US, "%.0f Lakhs per year", fees)
        }
    }

    private fun formatPackage(avgPackage: Double?): String {
        return when {
            avgPackage == null || avgPackage <= 0 -> "Package N/A"
            avgPackage >= 100_000 -> String.format(Locale.US, "Avg Pkg: %.1fL", avgPackage / 100_000)
            else -> String.format(Locale.US, "Avg Pkg: %.0f LPA", avgPackage)
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

    private fun loadBackgroundImage(
        context: android.content.Context,
        images: List<String>?,
        imageView: android.widget.ImageView
    ) {
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

    override fun getItemCount(): Int = colleges.size
}
