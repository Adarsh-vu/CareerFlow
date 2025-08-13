package com.arshcreations.careerflow.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.arshcreations.careerflow.R
import com.arshcreations.careerflow.databinding.FragmentSignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    // Launcher for Google Sign-In
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            Log.d("GoogleSignIn", "Firebase auth with Google:${account.id}")
            viewModel.signInWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w("GoogleSignIn", "Google sign in failed", e)
            Toast.makeText(requireContext(), "Google Sign-In failed.", Toast.LENGTH_SHORT).show()
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

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

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
            // Removed viewModel.startGoogleSignIn()
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }

        binding.createAccountText.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.authResult.observe(viewLifecycleOwner) { result ->
            binding.progressBar.isVisible = result == AuthResult.LOADING
            binding.signInButton.isEnabled = result != AuthResult.LOADING
            binding.signInWithGoogleButton.isEnabled = result != AuthResult.LOADING

            when (result) {
                AuthResult.SUCCESS -> findNavController().navigate(R.id.action_signInFragment_to_main_graph)
                AuthResult.FAILURE -> Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_LONG).show()
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
