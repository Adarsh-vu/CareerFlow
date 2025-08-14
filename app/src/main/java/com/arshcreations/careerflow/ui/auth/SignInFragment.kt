package com.arshcreations.careerflow.ui.auth

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.arshcreations.careerflow.R
import com.arshcreations.careerflow.databinding.FragmentSignInBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    private val oneTapClient by lazy { Identity.getSignInClient(requireActivity()) }

    private val oneTapSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken
                if (idToken != null) {
                    viewModel.signInWithGoogle(idToken)
                } else {
                    Log.w("OneTapSignIn", "Google ID token was null.")
                    Toast.makeText(requireContext(), "Google Sign-In failed.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                when (e.statusCode) {
                    CommonStatusCodes.CANCELED -> {
                        Log.d("OneTapSignIn", "One-tap sign in canceled.")
                    }
                    else -> {
                        Log.w("OneTapSignIn", "Google sign in failed", e)
                        Toast.makeText(requireContext(), "Google Sign-In failed.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

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
            beginOneTapSignIn()
        }

        binding.createAccountText.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
    }

    private fun beginOneTapSignIn() {
        val signInRequest = BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent).build()
                oneTapSignInLauncher.launch(intentSenderRequest)
            }
            .addOnFailureListener { e ->
                Log.e("OneTapSignIn", "Google One Tap sign-in failed", e)
                Toast.makeText(requireContext(), "Sign-in failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun observeViewModel() {
        viewModel.authResult.observe(viewLifecycleOwner) { result ->
            binding.progressBar.isVisible = result == AuthResult.LOADING
            binding.signInButton.isEnabled = result != AuthResult.LOADING
            binding.signInWithGoogleButton.isEnabled = result != AuthResult.LOADING

            when (result) {
                AuthResult.SUCCESS -> {
                    findNavController().navigate(R.id.action_signInFragment_to_main_graph)
                }
                AuthResult.FAILURE -> {
                    Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
