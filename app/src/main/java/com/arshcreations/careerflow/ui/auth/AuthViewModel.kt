package com.arshcreations.careerflow.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Sealed class representing authentication states
 */
sealed class AuthState {
    object Loading : AuthState()
    object Success : AuthState()
    data class Failure(val message: String) : AuthState()
}

/**
 * ViewModel for authentication operations
 * Handles sign up, sign in, Google Sign-In, and user profile management
 */
class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        // Check if user is already logged in
        if (auth.currentUser != null) {
            _authState.value = AuthState.Success
        }
    }

    /**
     * Sign up with email and password
     */
    fun signUp(email: String, password: String, name: String) {
        if (!validateInput(email, password, name)) return

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                // Create user account
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user

                if (user != null) {
                    // Update user profile with name
                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                    }
                    user.updateProfile(profileUpdates).await()

                    // Create user document in Firestore
                    val userMap = hashMapOf(
                        "uid" to user.uid,
                        "name" to name,
                        "email" to email,
                        "createdAt" to System.currentTimeMillis()
                    )
                    db.collection("users").document(user.uid).set(userMap).await()

                    _authState.value = AuthState.Success
                    Log.d(TAG, "Sign up successful for user: ${user.email}")
                } else {
                    _authState.value = AuthState.Failure("Failed to create user account")
                }

            } catch (e: FirebaseAuthException) {
                _authState.value = AuthState.Failure(getAuthErrorMessage(e))
                Log.e(TAG, "Sign up failed: ${e.message}", e)
            } catch (e: Exception) {
                _authState.value = AuthState.Failure("An unexpected error occurred")
                Log.e(TAG, "Sign up error: ${e.message}", e)
            }
        }
    }

    /**
     * Sign in with email and password
     */
    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Failure("Email and password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.Success
                Log.d(TAG, "Sign in successful for user: $email")

            } catch (e: FirebaseAuthException) {
                _authState.value = AuthState.Failure(getAuthErrorMessage(e))
                Log.e(TAG, "Sign in failed: ${e.message}", e)
            } catch (e: Exception) {
                _authState.value = AuthState.Failure("An unexpected error occurred")
                Log.e(TAG, "Sign in error: ${e.message}", e)
            }
        }
    }

    /**
     * Sign in with Google credential
     */
    fun signInWithGoogle(idToken: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                val user = result.user

                if (user != null) {
                    // Check if user document exists in Firestore
                    val userDoc = db.collection("users").document(user.uid).get().await()

                    if (!userDoc.exists()) {
                        // Create new user document
                        val userMap = hashMapOf(
                            "uid" to user.uid,
                            "name" to (user.displayName ?: "User"),
                            "email" to (user.email ?: ""),
                            "photoUrl" to (user.photoUrl?.toString() ?: ""),
                            "createdAt" to System.currentTimeMillis()
                        )
                        db.collection("users").document(user.uid).set(userMap).await()
                    }

                    _authState.value = AuthState.Success
                    Log.d(TAG, "Google sign-in successful for user: ${user.email}")
                } else {
                    _authState.value = AuthState.Failure("Failed to sign in with Google")
                }

            } catch (e: FirebaseAuthException) {
                _authState.value = AuthState.Failure(getAuthErrorMessage(e))
                Log.e(TAG, "Google sign-in failed: ${e.message}", e)
            } catch (e: Exception) {
                _authState.value = AuthState.Failure("An unexpected error occurred")
                Log.e(TAG, "Google sign-in error: ${e.message}", e)
            }
        }
    }

    /**
     * Reset authentication state
     * Called from Fragment's onStop() to prevent showing old state
     */
    fun resetAuthState() {
        _authState.value = null
    }

    /**
     * Sign out current user
     */
    fun signOut() {
        auth.signOut()
        _authState.value = null
        Log.d(TAG, "User signed out")
    }

    /**
     * Validate input fields
     */
    private fun validateInput(email: String, password: String, name: String): Boolean {
        return when {
            name.isBlank() -> {
                _authState.value = AuthState.Failure("Name cannot be empty")
                false
            }
            email.isBlank() -> {
                _authState.value = AuthState.Failure("Email cannot be empty")
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _authState.value = AuthState.Failure("Invalid email format")
                false
            }
            password.length < 6 -> {
                _authState.value = AuthState.Failure("Password must be at least 6 characters")
                false
            }
            else -> true
        }
    }

    /**
     * Get user-friendly error messages
     */
    private fun getAuthErrorMessage(exception: FirebaseAuthException): String {
        return when (exception.errorCode) {
            "ERROR_INVALID_EMAIL" -> "Invalid email address"
            "ERROR_WRONG_PASSWORD" -> "Incorrect password"
            "ERROR_USER_NOT_FOUND" -> "No account found with this email"
            "ERROR_USER_DISABLED" -> "This account has been disabled"
            "ERROR_EMAIL_ALREADY_IN_USE" -> "Email is already registered"
            "ERROR_WEAK_PASSWORD" -> "Password is too weak"
            "ERROR_NETWORK_REQUEST_FAILED" -> "Network error. Please check your connection"
            else -> exception.message ?: "Authentication failed"
        }
    }

    companion object {
        private const val TAG = "AuthViewModel"
    }
}
