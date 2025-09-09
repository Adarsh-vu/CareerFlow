package com.arshcreations.careerflow.ui.colleges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.arshcreations.careerflow.R
import com.arshcreations.careerflow.databinding.BottomSheetFilterBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import java.util.Locale

class FilterBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetFilterBinding? = null
    private val binding get() = _binding!!

    private val activeFilters: ActiveFilters by lazy {
        arguments?.getParcelable("active_filters") ?: ActiveFilters()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDropdowns()
        setupListeners()
        restoreFilterState()
    }

    private fun restoreFilterState() {
        // Restore stream selection from ChipGroup
        val streamChipId = when (activeFilters.stream) {
            getString(R.string.filter_stream_engineering) -> R.id.chip_engineering
            getString(R.string.filter_stream_management) -> R.id.chip_management
            getString(R.string.filter_stream_medical) -> R.id.chip_medical
            else -> R.id.chip_any
        }
        binding.streamChipGroup.check(streamChipId)

        binding.stateAutocomplete.setText(activeFilters.state ?: getString(R.string.filter_option_any), false)
        binding.examAutocomplete.setText(activeFilters.exam ?: getString(R.string.filter_option_any), false)

        if (activeFilters.maxFees != -1f) {
            binding.feesSlider.value = activeFilters.maxFees
        } else {
            binding.feesSlider.value = binding.feesSlider.valueTo
        }
    }

    private fun setupDropdowns() {
        val states = resources.getStringArray(R.array.indian_states)
        val stateAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, states)
        binding.stateAutocomplete.setAdapter(stateAdapter)

        val exams = resources.getStringArray(R.array.entrance_exams)
        val examAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, exams)
        binding.examAutocomplete.setAdapter(examAdapter)
    }

    private fun setupListeners() {
        binding.feesSlider.addOnChangeListener { _, value, _ ->
            if (value >= binding.feesSlider.valueTo) {
                binding.feesValueText.text = getString(R.string.filter_option_any)
            } else {
                binding.feesValueText.text = String.format(Locale.US, "Up to %.1f Lakhs", value)
            }
        }

        binding.applyFiltersButton.setOnClickListener {
            val selectedChipId = binding.streamChipGroup.checkedChipId
            val selectedChip = view?.findViewById<Chip>(selectedChipId)
            val selectedStream = selectedChip?.text?.toString()?.takeIf { !it.equals(getString(R.string.filter_option_any), ignoreCase = true) }

            val selectedState = binding.stateAutocomplete.text.toString().takeIf { !it.equals(getString(R.string.filter_option_any), ignoreCase = true) }
            val selectedExam = binding.examAutocomplete.text.toString().takeIf { !it.equals(getString(R.string.filter_option_any), ignoreCase = true) }
            val maxFees = binding.feesSlider.value.takeIf { it < binding.feesSlider.valueTo } ?: -1f

            val newFilters = activeFilters.copy(
                stream = selectedStream,
                state = selectedState,
                exam = selectedExam,
                maxFees = maxFees
            )

            setFragmentResult("filter_request", bundleOf("new_filters" to newFilters))
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "FilterBottomSheet"
        fun newInstance(activeFilters: ActiveFilters): FilterBottomSheetFragment {
            val fragment = FilterBottomSheetFragment()
            fragment.arguments = bundleOf("active_filters" to activeFilters)
            return fragment
        }
    }
}