package com.arshcreations.careerflow.ui.colleges

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.arshcreations.careerflow.R
import com.arshcreations.careerflow.data.models.College
import com.arshcreations.careerflow.databinding.FragmentCollegeDetailBinding
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class CollegeDetailFragment : Fragment() {

    private var _binding: FragmentCollegeDetailBinding? = null
    private val binding get() = _binding!!

    private val args: CollegeDetailFragmentArgs by navArgs()

    private val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val outputFormat = SimpleDateFormat("MMM dd", Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollegeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the toolbar with the NavController for a working "Up" button (back arrow)
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        NavigationUI.setupWithNavController(binding.toolbar, findNavController())

        val college = args.college
        populateUi(college)
    }

    private fun populateUi(college: College) {
        // Set the title on the collapsing toolbar
        binding.toolbarLayout.title = college.name

        // Load the main image using Glide
        college.images.firstOrNull()?.let { imageUrl ->
            Glide.with(this)
                .load(imageUrl)
                .into(binding.detailCollegeImage)
        }

        // Populate the main content
        binding.detailCollegeName.text = college.name
        binding.detailCollegeLocation.text = "${college.city}, ${college.state}"
        binding.detailCollegeStream.text = college.stream
        binding.detailCollegeDescription.text = college.description

        // Add icons programmatically
        setTextViewDrawable(binding.detailCollegeLocation, R.drawable.ic_location_on, 20)
        setTextViewDrawable(binding.detailCollegeFees, R.drawable.ic_currency_rupee, 20)
        setTextViewDrawable(binding.detailCollegePackage, R.drawable.ic_placement, 20)

        // Format fees and package using helper function
        binding.detailCollegeFees.text = formatValue(college.fees, "%.1fL per year")
        binding.detailCollegePackage.text = formatValue(college.avgPackage, "%.1f LPA")

        // Populate Admission Details card
        binding.detailNirfRank.text = if (college.nirfRanking > 0) college.nirfRanking.toString() else "N/A"
        binding.detailClosingRank.text = if (college.closingRank > 0) college.closingRank.toString() else "N/A"
        binding.detailExams.text = college.requiredExams.joinToString(", ").ifEmpty { "N/A" }
        binding.detailAdmissionDates.text = formatAdmissionDates(college.admissionStart, college.admissionEnd)
    }

    // Helper function to programmatically set drawable icons on TextViews
    private fun setTextViewDrawable(textView: TextView, drawableRes: Int, sizeDp: Int) {
        val drawable = ContextCompat.getDrawable(requireContext(), drawableRes)
        val sizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            sizeDp.toFloat(),
            resources.displayMetrics
        ).toInt()
        drawable?.setBounds(0, 0, sizePx, sizePx)
        textView.setCompoundDrawablesRelative(drawable, null, null, null)
    }

    // Helper function to format fee and package values
    private fun formatValue(value: Double, format: String): String {
        return when {
            value <= 0 -> "N/A"
            else -> String.format(Locale.US, format, value / 100_000)
        }
    }

    // Helper function to format admission dates
    private fun formatAdmissionDates(startDate: String, endDate: String): String {
        if (startDate.isBlank() || endDate.isBlank()) return "N/A"
        return try {
            val start = inputFormat.parse(startDate)
            val end = inputFormat.parse(endDate)
            "${outputFormat.format(start!!)} - ${outputFormat.format(end!!)}"
        } catch (e: Exception) {
            "N/A"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}