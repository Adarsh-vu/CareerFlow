package com.arshcreations.careerflow.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

enum class AuthResult {
    SUCCESS,
    FAILURE,
    LOADING
}

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _authResult = MutableLiveData<AuthResult?>()
    val authResult: LiveData<AuthResult?> = _authResult

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
                val user = result.user

                if (user != null) {
                    @Suppress("DEPRECATION")
                    val profileUpdates = userProfileChangeRequest { displayName = name }
                    user.updateProfile(profileUpdates).await()

                    val userMap = hashMapOf(
                        "uid" to user.uid,
                        "name" to name,
                        "email" to email
                    )
                    db.collection("users").document(user.uid).set(userMap).await()

                    _authResult.value = AuthResult.SUCCESS
                } else {
                    _authResult.value = AuthResult.FAILURE
                }
            } catch (_: Exception) {
                _authResult.value = AuthResult.FAILURE
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authResult.value = AuthResult.LOADING
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                val user = result.user

                if (user != null) {
                    val isNewUser = result.additionalUserInfo?.isNewUser ?: false
                    if (isNewUser) {
                        val userMap = hashMapOf(
                            "uid" to user.uid,
                            "name" to user.displayName,
                            "email" to user.email
                        )
                        db.collection("users").document(user.uid).set(userMap).await()
                    }
                    _authResult.value = AuthResult.SUCCESS
                } else {
                    _authResult.value = AuthResult.FAILURE
                }
            } catch (_: Exception) {
                _authResult.value = AuthResult.FAILURE
            }
        }
    }

    fun resetAuthResult() {
        _authResult.value = null
    }
}