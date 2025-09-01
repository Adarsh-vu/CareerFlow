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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollegesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.colleges.observe(viewLifecycleOwner) { collegeList ->
            if (!collegeList.isNullOrEmpty()) {
                binding.collegesRecyclerView.adapter = CollegesAdapter(collegeList)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            binding.errorText.isVisible = errorMessage != null
            binding.errorText.text = errorMessage
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
