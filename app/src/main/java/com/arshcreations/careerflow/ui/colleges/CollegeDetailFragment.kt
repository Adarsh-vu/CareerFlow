package com.arshcreations.careerflow.ui.colleges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.arshcreations.careerflow.databinding.FragmentCollegeDetailBinding
import com.bumptech.glide.Glide
import java.util.Locale

class CollegeDetailFragment : Fragment() {

    private var _binding: FragmentCollegeDetailBinding? = null
    private val binding get() = _binding!!

    // Use Safe Args to get the passed college object
    private val args: CollegeDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollegeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val college = args.college
        populateUi(college)
    }

    private fun populateUi(college: College) {
        binding.toolbarLayout.title = college.name
        binding.detailCollegeLocation.text = "${college.city}, ${college.state}"
        binding.detailCollegeStream.text = college.stream
        binding.detailCollegeDescription.text = college.description

        // Format fees and package
        binding.detailCollegeFees.text = formatValue(college.fees, "%.1fL p.a.")
        binding.detailCollegePackage.text = formatValue(college.avgPackage, "%.1f LPA")

        // Load the image using Glide
        college.images?.firstOrNull()?.let { imageUrl ->
            Glide.with(this)
                .load(imageUrl)
                .into(binding.detailCollegeImage)
        }
    }

    private fun formatValue(value: Double?, format: String): String {
        return when {
            value == null || value <= 0 -> "N/A"
            else -> String.format(Locale.US, format, value / 100_000)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}