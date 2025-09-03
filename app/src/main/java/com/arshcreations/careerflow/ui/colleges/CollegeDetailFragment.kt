package com.arshcreations.careerflow.ui.colleges

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.arshcreations.careerflow.R
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

        NavigationUI.setupWithNavController(binding.toolbar, findNavController())

        val college = args.college
        populateUi(college)
    }

    private fun populateUi(college: College) {
        binding.toolbarLayout.title = getString(R.string.college_detail_title)
        binding.detailCollegeName.text = college.name
        binding.detailCollegeLocation.text = "${college.city}, ${college.state}"
        binding.detailCollegeStream.text = college.stream
        binding.detailCollegeDescription.text = college.description

        // Standardize icon sizes programmatically
        setTextViewDrawableSize(binding.detailCollegeLocation, R.drawable.ic_location_on, 24)
        setTextViewDrawableSize(binding.detailCollegeFees, R.drawable.ic_currency_rupee, 24)
        setTextViewDrawableSize(binding.detailCollegePackage, R.drawable.ic_placement, 24)

        // Set tints for icons
        binding.detailCollegeFees.compoundDrawables[0]?.setTint(getThemeColor(R.attr.colorSuccess))
        binding.detailCollegePackage.compoundDrawables[0]?.setTint(getThemeColor(R.attr.colorSuccess))

        // Format fees and package
        binding.detailCollegeFees.text = formatValue(college.fees, "%.1fL per year")
        binding.detailCollegePackage.text = formatValue(college.avgPackage, "%.1f LPA")

        // Populate Admission Details
        binding.detailNirfRank.text = college.nirfRanking?.toString() ?: "N/A"
        binding.detailClosingRank.text = college.closingRank?.toString() ?: "N/A"
        binding.detailExams.text = college.requiredExams?.joinToString(", ") ?: "N/A"
        binding.detailAdmissionDates.text = formatAdmissionDates(college.admissionStart, college.admissionEnd)

        // Load the image using Glide
        college.images?.firstOrNull()?.let { imageUrl ->
            Glide.with(this)
                .load(imageUrl)
                .into(binding.detailCollegeImage)
        }
    }

    private fun setTextViewDrawableSize(textView: TextView, drawableRes: Int, sizeDp: Int) {
        val drawable = ContextCompat.getDrawable(requireContext(), drawableRes)
        val sizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sizeDp.toFloat(), resources.displayMetrics).toInt()
        drawable?.setBounds(0, 0, sizePx, sizePx)
        textView.setCompoundDrawables(drawable, null, null, null)
    }

    private fun getThemeColor(attr: Int): Int {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    private fun formatValue(value: Double?, format: String): String {
        return when {
            value == null || value <= 0 -> "N/A"
            else -> String.format(Locale.US, format, value / 100_000)
        }
    }

    private fun formatAdmissionDates(startDate: String?, endDate: String?): String {
        if (startDate.isNullOrBlank() || endDate.isNullOrBlank()) return "N/A"
        return try {
            val start = inputFormat.parse(startDate)
            val end = inputFormat.parse(endDate)
            // THIS IS THE FIX: Corrected the typo from 'outputformat' to 'outputFormat'
            "${outputFormat.format(start)} - ${outputFormat.format(end)}"
        } catch (e: Exception) {
            "N/A"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}