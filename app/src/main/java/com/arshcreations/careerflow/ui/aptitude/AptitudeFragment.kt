package com.arshcreations.careerflow.ui.aptitude

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.arshcreations.careerflow.R
import com.arshcreations.careerflow.databinding.FragmentAptitudeBinding

class AptitudeFragment : Fragment() {

    private var _binding: FragmentAptitudeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AptitudeViewModel by viewModels()
    private var questionPagerAdapter: QuestionPagerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAptitudeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.questionsViewPager.isUserInputEnabled = false
        observeViewModel()
        binding.nextButton.setOnClickListener {
            viewModel.moveToNextQuestion()
        }
    }

    private fun observeViewModel() {
        viewModel.testState.observe(viewLifecycleOwner) { state ->
            // Hide all main views by default, then show the correct one
            binding.progressBar.isVisible = false
            binding.testContentGroup.isVisible = false
            binding.errorText.isVisible = false
            binding.analyzingGroup.isVisible = false

            when (state) {
                is AptitudeTestState.Loading -> {
                    binding.progressBar.isVisible = true
                }
                is AptitudeTestState.TestInProgress -> {
                    binding.testContentGroup.isVisible = true

                    if (binding.questionsViewPager.adapter == null) {
                        questionPagerAdapter = QuestionPagerAdapter(state.questions) { questionId, optionIndex ->
                            viewModel.recordAnswer(questionId, optionIndex)
                        }
                        binding.questionsViewPager.adapter = questionPagerAdapter
                    }

                    binding.questionsViewPager.currentItem = state.currentQuestionIndex

                    val totalQuestions = state.questions.size
                    binding.progressIndicator.max = totalQuestions
                    binding.progressIndicator.progress = state.currentQuestionIndex + 1
                    binding.progressText.text = "Question ${state.currentQuestionIndex + 1}/$totalQuestions"

                    binding.nextButton.text = if (state.currentQuestionIndex == totalQuestions - 1) {
                        getString(R.string.action_submit)
                    } else {
                        getString(R.string.action_next)
                    }
                }
                is AptitudeTestState.Analyzing -> {
                    // Show the "Analyzing..." overlay
                    binding.analyzingGroup.isVisible = true
                }
                is AptitudeTestState.AnalysisSuccess -> {
                    // Navigate to the results screen with the AI-generated report
                    val action = AptitudeFragmentDirections.actionAptitudeFragmentToAptitudeResultsFragment(state.result)
                    findNavController().navigate(action)
                }
                is AptitudeTestState.Failure -> {
                    binding.errorText.isVisible = true
                    binding.errorText.text = state.errorMessage
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Prevent memory leaks by nulling out the adapter reference
        binding.questionsViewPager.adapter = null
        questionPagerAdapter = null
        _binding = null
    }
}

