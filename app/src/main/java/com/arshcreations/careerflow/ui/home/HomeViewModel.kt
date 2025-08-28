package com.arshcreations.careerflow.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class HomeViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    init {
        loadUserName()
    }

    private fun loadUserName() {
        val user = auth.currentUser
        _userName.value = user?.displayName ?: "User"
    }
}