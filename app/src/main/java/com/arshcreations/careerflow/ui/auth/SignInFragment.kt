package com.arshcreations.careerflow.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.arshcreations.careerflow.R
import com.arshcreations.careerflow.databinding.FragmentSignInBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()
    private val credentialManager by lazy { CredentialManager.create(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        binding.signInButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.signIn(email, password)
            } else {
                Toast.makeText(requireContext(), "Please enter email and password.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signInWithGoogleButton.setOnClickListener {
            beginGoogleSignIn()
        }

        binding.createAccountText.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
    }

    private fun beginGoogleSignIn() {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.default_web_client_id))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(requireActivity(), request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    viewModel.signInWithGoogle(googleIdTokenCredential.idToken)
                } else {
                    Log.w("SignInFragment", "Unexpected credential type or is not a Google ID token.")
                    Toast.makeText(requireContext(), "Google Sign-In failed.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: GetCredentialException) {
                // Important change: We now log the specific error
                Log.e("SignInFragment", "GetCredentialException: ${e.message}", e)
                Toast.makeText(requireContext(), "Sign-in failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun observeViewModel() {
        // We now observe the new 'authState' LiveData
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            binding.progressBar.isVisible = state is AuthState.Loading
            binding.signInButton.isEnabled = state !is AuthState.Loading
            binding.signInWithGoogleButton.isEnabled = state !is AuthState.Loading

            when (state) {
                is AuthState.Success -> {
                    findNavController().navigate(R.id.action_signInFragment_to_main_graph)
                }
                is AuthState.Failure -> {
                    // Important change: We now show the detailed error message
                    Toast.makeText(requireContext(), "Error: ${state.message}", Toast.LENGTH_LONG).show()
                }
                else -> {} // For Loading or null state
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // Reset the state when the fragment is not visible to avoid showing old toasts
        viewModel.resetAuthState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}