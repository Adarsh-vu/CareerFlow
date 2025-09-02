package com.arshcreations.careerflow.ui.colleges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.arshcreations.careerflow.databinding.FragmentCollegesBinding

class CollegesFragment : Fragment() {

    private var _binding: FragmentCollegesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CollegesViewModel by viewModels()
    private var collegesAdapter: CollegesAdapter? = null
    private var currentStreamFilter: String? = null
    private var currentLocationFilter: String? = null
    private var currentMaxFeesFilter: Float = -1f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollegesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        setupFilterResultListener()
    }

    private fun setupRecyclerView() {
        collegesAdapter = CollegesAdapter(emptyList()) { college ->
            // This is the correct navigation logic
            val action = CollegesFragmentDirections.actionCollegesFragmentToCollegeDetailFragment(college)
            findNavController().navigate(action)
        }
        binding.collegesRecyclerView.adapter = collegesAdapter
    }

    private fun setupObservers() {
        viewModel.colleges.observe(viewLifecycleOwner) { collegeList ->
            val nonNullList = collegeList ?: emptyList()
            collegesAdapter?.updateColleges(nonNullList)
            updateResultsCount(nonNullList.size)

            val isLoading = viewModel.isLoading.value ?: false
            if (!isLoading && viewModel.error.value == null && nonNullList.isEmpty()) {
                binding.errorText.text = "No colleges found matching your criteria."
                binding.errorText.isVisible = true
            } else if (viewModel.error.value == null){
                binding.errorText.isVisible = false
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.collegesRecyclerView.isVisible = !isLoading
            binding.resultsCount.isVisible = !isLoading
            if(isLoading) binding.errorText.isVisible = false
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            val collegeList = viewModel.colleges.value
            if (errorMessage != null && collegeList.isNullOrEmpty()) {
                binding.errorText.text = errorMessage
                binding.errorText.isVisible = true
                binding.collegesRecyclerView.isVisible = false
                binding.resultsCount.isVisible = false
            }
        }
    }

    private fun setupClickListeners() {
        binding.filterButton.setOnClickListener {
            FilterBottomSheetFragment().show(childFragmentManager, FilterBottomSheetFragment.TAG)
        }

        binding.searchEditText.addTextChangedListener { text ->
            val query = text.toString().trim()
            viewModel.applyFilters(query, currentStreamFilter, currentLocationFilter, currentMaxFeesFilter)
        }
    }

    private fun setupFilterResultListener() {
        childFragmentManager.setFragmentResultListener("filter_request", viewLifecycleOwner) { _, bundle ->
            currentStreamFilter = bundle.getString("selected_stream")
            currentLocationFilter = bundle.getString("selected_location")
            currentMaxFeesFilter = bundle.getFloat("max_fees", -1f)

            val currentQuery = binding.searchEditText.text.toString().trim()
            viewModel.applyFilters(currentQuery, currentStreamFilter, currentLocationFilter, currentMaxFeesFilter)
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
        binding.collegesRecyclerView.adapter = null
        collegesAdapter = null
        _binding = null
    }
}