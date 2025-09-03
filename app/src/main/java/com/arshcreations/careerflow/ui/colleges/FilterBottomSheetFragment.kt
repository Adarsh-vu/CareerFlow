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
    }

    private fun setupDropdowns() {
        val states = resources.getStringArray(R.array.indian_states)
        val stateAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, states)
        binding.stateAutocomplete.setAdapter(stateAdapter)
        binding.stateAutocomplete.setText(states[0], false)

        val exams = resources.getStringArray(R.array.entrance_exams)
        val examAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, exams)
        binding.examAutocomplete.setAdapter(examAdapter)
        binding.examAutocomplete.setText(exams[0], false)
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
            // Get Stream
            val selectedChipId = binding.streamChipGroup.checkedChipId
            val selectedChip = binding.root.findViewById<Chip>(selectedChipId)
            val selectedStream = if (selectedChip.text.toString().equals(getString(R.string.filter_option_any), ignoreCase = true)) {
                null
            } else {
                selectedChip.text.toString()
            }

            // Get State
            val selectedStateText = binding.stateAutocomplete.text.toString()
            val selectedState = if (selectedStateText.equals(getString(R.string.filter_option_any), ignoreCase = true) || selectedStateText.isBlank()) {
                null
            } else {
                selectedStateText
            }

            // Get Exam
            val selectedExamText = binding.examAutocomplete.text.toString()
            val selectedExam = if (selectedExamText.equals(getString(R.string.filter_option_any), ignoreCase = true) || selectedExamText.isBlank()) {
                null
            } else {
                selectedExamText
            }

            // Get Max Fees
            val maxFeesValue = binding.feesSlider.value
            val maxFees = if (maxFeesValue >= binding.feesSlider.valueTo) -1f else maxFeesValue

            // Send all results back
            setFragmentResult("filter_request", bundleOf(
                "selected_stream" to selectedStream,
                "selected_state" to selectedState,
                "selected_exam" to selectedExam,
                "max_fees" to maxFees
            ))

            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "FilterBottomSheet"
    }
}