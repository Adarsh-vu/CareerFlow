package com.arshcreations.careerflow.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class HomeViewModel : ViewModel() {

    // This will hold the user's name
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    init {
        // Get the current user from Firebase when the ViewModel is created
        val user = FirebaseAuth.getInstance().currentUser

        // If the user exists and has a name, update our LiveData
        if (user != null) {
            _userName.value = user.displayName ?: "User"
        } else {
            _userName.value = "Welcome!"
        }
    }
}