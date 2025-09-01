package com.arshcreations.careerflow.ui.colleges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.arshcreations.careerflow.databinding.FragmentCollegesBinding

class CollegesFragment : Fragment() {

    private var _binding: FragmentCollegesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CollegesViewModel by viewModels()
    private var collegesAdapter: CollegesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollegesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.colleges.observe(viewLifecycleOwner) { collegeList ->
            if (!collegeList.isNullOrEmpty()) {
                collegesAdapter = CollegesAdapter(collegeList) { college ->
                    // Handle college click - navigate to details
                    // TODO: Implement navigation to college details
                }
                binding.collegesRecyclerView.adapter = collegesAdapter

                // Update results count
                updateResultsCount(collegeList.size)

                // Hide error state
                binding.errorText.isVisible = false
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading

            // Hide/show other views based on loading state
            binding.collegesRecyclerView.isVisible = !isLoading
            binding.resultsCount.isVisible = !isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            binding.errorText.isVisible = errorMessage != null
            binding.errorText.text = errorMessage

            // Hide other views when showing error
            binding.collegesRecyclerView.isVisible = errorMessage == null
            binding.resultsCount.isVisible = errorMessage == null
        }
    }

    private fun setupClickListeners() {
        // Filter button click
        binding.filterButton.setOnClickListener {
            // TODO: Implement filter functionality
            // You can show a bottom sheet or navigate to filter screen
        }

        // Search functionality
        binding.searchEditText.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                // TODO: Implement search functionality
                // viewModel.searchColleges(query)
            }
            true
        }
    }

    private fun updateResultsCount(count: Int) {
        binding.resultsCount.text = if (count == 1) {
            "$count college found"
        } else {
            "$count colleges found"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        collegesAdapter = null
        _binding = null
    }
}