package com.arshcreations.careerflow.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

enum class AuthResult {
    SUCCESS,
    FAILURE,
    LOADING
}

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authResult = MutableLiveData<AuthResult>()
    val authResult: LiveData<AuthResult> = _authResult

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = AuthResult.LOADING
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _authResult.value = AuthResult.SUCCESS
            } catch (_: Exception) {
                _authResult.value = AuthResult.FAILURE
            }
        }
    }

    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = AuthResult.LOADING
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val profileUpdates = userProfileChangeRequest { displayName = name }
                result.user?.updateProfile(profileUpdates)?.await()
                _authResult.value = AuthResult.SUCCESS
            } catch (_: Exception) {
                _authResult.value = AuthResult.FAILURE
            }
        }
    }

    // This function is perfect and will be called by the fragment
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authResult.value = AuthResult.LOADING
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await()
                _authResult.value = AuthResult.SUCCESS
            } catch (_: Exception) {
                _authResult.value = AuthResult.FAILURE
            }
        }
    }
}
