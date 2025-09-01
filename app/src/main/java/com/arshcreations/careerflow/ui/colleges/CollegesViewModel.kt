package com.arshcreations.careerflow.ui.colleges

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CollegesViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _colleges = MutableLiveData<List<College>>()
    val colleges: LiveData<List<College>> = _colleges

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        fetchColleges()
    }

    private fun fetchColleges() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = db.collection("colleges").get().await()
                val collegeList = snapshot.toObjects(College::class.java)
                _colleges.value = collegeList
                _error.value = null
            } catch (e: Exception) {
                Log.e("CollegesViewModel", "Error fetching colleges", e)
                _error.value = "Failed to load colleges. Please check your connection."
            } finally {
                _isLoading.value = false
            }
        }
    }
}
