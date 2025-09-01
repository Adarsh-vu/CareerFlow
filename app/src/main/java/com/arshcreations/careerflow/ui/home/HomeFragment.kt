package com.arshcreations.careerflow.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.arshcreations.careerflow.R
import com.arshcreations.careerflow.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.taglineText.isSelected = true

        viewModel.userName.observe(viewLifecycleOwner) { name ->
            binding.welcomeText.text = getString(R.string.dash_welcome, name)
        }

        // Card click listeners
        binding.collegeFinderCard.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_collegesFragment)
        }

        binding.aptitudeTestCard.setOnClickListener {
            Toast.makeText(requireContext(), "Aptitude Test clicked!", Toast.LENGTH_SHORT).show()
        }

        binding.aiGuideCard.setOnClickListener {
            Toast.makeText(requireContext(), "AI Guide clicked!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}