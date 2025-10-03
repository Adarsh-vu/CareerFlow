package com.arshcreations.careerflow.ui.aptitude

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

    private lateinit var tips: Array<String>
    private var currentTipIndex = 0
    private val tipHandler = Handler(Looper.getMainLooper())
    private val tipRunnable = object : Runnable {
        override fun run() {
            if (_binding == null) return // Prevent crash if view is destroyed
            binding.tipText.animate().alpha(0f).setDuration(300).withEndAction {
                currentTipIndex = (currentTipIndex + 1) % tips.size
                binding.tipText.text = tips[currentTipIndex]
                binding.tipText.animate().alpha(1f).setDuration(300).start()
            }.start()
            tipHandler.postDelayed(this, 10000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAptitudeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tips = resources.getStringArray(R.array.aptitude_solving_tips)
        binding.questionsViewPager.isUserInputEnabled = false
        observeViewModel()
        binding.nextButton.setOnClickListener {
            viewModel.moveToNextQuestion()
        }
    }

    private fun observeViewModel() {
        viewModel.testState.observe(viewLifecycleOwner) { state ->
            // Ensure binding is not null before accessing views
            if (_binding == null) return@observe

            tipHandler.removeCallbacks(tipRunnable)
            binding.tipText.alpha = 1f

            // Hide all views by default
            binding.loadingCard.isVisible = false
            binding.testContentGroup.isVisible = false
            binding.errorText.isVisible = false
            binding.analyzingGroup.isVisible = false

            when (state) {
                is AptitudeTestState.Loading -> {
                    // Only show the single, self-contained loading card
                    binding.loadingCard.isVisible = true
                    currentTipIndex = 0
                    binding.tipText.text = tips[currentTipIndex]
                    tipHandler.postDelayed(tipRunnable, 10000)
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
                    binding.progressText.text = getString(
                        R.string.question_progress,
                        state.currentQuestionIndex + 1,
                        totalQuestions
                    )

                    binding.nextButton.text = if (state.currentQuestionIndex == totalQuestions - 1) {
                        getString(R.string.action_submit)
                    } else {
                        getString(R.string.action_next)
                    }
                }
                is AptitudeTestState.Analyzing -> {
                    binding.analyzingGroup.isVisible = true
                }
                is AptitudeTestState.AnalysisSuccess -> {
                    // Safe navigation check
                    findNavController().currentDestination?.id?.let {
                        if (it == R.id.aptitudeFragment) {
                            val action = AptitudeFragmentDirections.actionAptitudeFragmentToAptitudeResultsFragment(state.result)
                            findNavController().navigate(action)
                        }
                    }
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
        tipHandler.removeCallbacks(tipRunnable)
        binding.questionsViewPager.adapter = null
        questionPagerAdapter = null
        _binding = null
    }
}