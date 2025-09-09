package com.arshcreations.careerflow.ui.colleges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.arshcreations.careerflow.data.models.College
import com.arshcreations.careerflow.databinding.FragmentCollegesBinding

class CollegesFragment : Fragment() {

    private var _binding: FragmentCollegesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CollegesViewModel by viewModels()
    private val args: CollegesFragmentArgs by navArgs()
    private lateinit var collegesAdapter: CollegesAdapter

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
        setupListeners()
        observeViewModel()

        if (args.isPersonalizedSearch) {
            viewModel.fetchCollegesBasedOnProfile()
        } else {
            viewModel.loadInitialData()
        }
    }

    private fun setupListeners() {
        // CORRECTED ID: filter_button
        binding.filterButton.setOnClickListener {
            val filterSheet = FilterBottomSheetFragment.newInstance(viewModel.getCurrentFilters())
            filterSheet.show(childFragmentManager, FilterBottomSheetFragment.TAG)
        }

        setFragmentResultListener("filter_request") { _, bundle ->
            val newFilters = bundle.getParcelable<ActiveFilters>("new_filters")
            if (newFilters != null) {
                // CORRECTED ID: search_edit_text
                binding.searchEditText.setText(newFilters.query)
                viewModel.onFiltersApplied(newFilters)
            }
        }

        // CORRECTED ID: search_edit_text
        binding.searchEditText.addTextChangedListener { editable ->
            viewModel.onQueryChanged(editable.toString())
        }
    }

    private fun setupRecyclerView() {
        collegesAdapter = CollegesAdapter { college ->
            val action = CollegesFragmentDirections.actionCollegesFragmentToCollegeDetailFragment(college)
            findNavController().navigate(action)
        }
        // CORRECTED ID: colleges_recycler_view
        binding.collegesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = collegesAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.colleges.observe(viewLifecycleOwner) { colleges ->
            collegesAdapter.submitList(colleges)

            binding.resultsCount.text = "${colleges.size} colleges found"

            binding.errorText.isVisible = colleges.isEmpty() && viewModel.isLoading.value == false
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->

            binding.progressBar.isVisible = isLoading
            if (isLoading) {
                binding.errorText.isVisible = false
            }
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                binding.errorText.text = it
                binding.errorText.isVisible = true
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}