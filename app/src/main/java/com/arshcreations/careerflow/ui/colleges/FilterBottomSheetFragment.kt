package com.arshcreations.careerflow.ui.colleges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
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

        // Update fee label as slider moves
        binding.feesSlider.addOnChangeListener { _, value, _ ->
            if (value >= binding.feesSlider.valueTo) {
                binding.feesValueText.text = "Any"
            } else {
                binding.feesValueText.text = String.format(Locale.US, "Up to %.1f Lakhs", value)
            }
        }

        binding.applyFiltersButton.setOnClickListener {
            // Get selected stream
            val selectedChipId = binding.streamChipGroup.checkedChipId
            val selectedChip = view.findViewById<Chip>(selectedChipId)
            val selectedStream = if (selectedChip.text.toString().equals("Any", ignoreCase = true)) {
                null
            } else {
                selectedChip.text.toString()
            }

            // Get location
            val selectedLocation = binding.locationEditText.text.toString().trim()

            // Get max fees
            val maxFeesValue = binding.feesSlider.value
            val maxFees = if (maxFeesValue >= binding.feesSlider.valueTo) {
                -1f // Used -1 to indicate no budget limit
            } else {
                maxFeesValue
            }

            // Send all results back to CollegesFragment
            setFragmentResult("filter_request", bundleOf(
                "selected_stream" to selectedStream,
                "selected_location" to selectedLocation,
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