package com.arshcreations.careerflow.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.arshcreations.careerflow.R
import com.arshcreations.careerflow.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()

        binding.createAccountButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                if (password.length < 6) {
                    Toast.makeText(requireContext(), "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.signUp(name, email, password)
                }
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        // Updated to observe the new 'authState'
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            binding.progressBar.isVisible = state is AuthState.Loading
            binding.createAccountButton.isEnabled = state !is AuthState.Loading

            when (state) {
                is AuthState.Success -> {
                    Toast.makeText(requireContext(), "Account created successfully!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_signUpFragment_to_main_graph)
                    // Updated to call the new reset function
                    viewModel.resetAuthState()
                }
                is AuthState.Failure -> {
                    // Updated to show the detailed error message
                    Toast.makeText(requireContext(), "Sign up failed: ${state.message}", Toast.LENGTH_LONG).show()
                }
                else -> {
                    // For Loading or null state
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}