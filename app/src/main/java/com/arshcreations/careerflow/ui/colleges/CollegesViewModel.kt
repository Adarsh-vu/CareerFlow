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

    private var allColleges: List<College> = emptyList()

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
                allColleges = db.collection("colleges").get().await().toObjects(College::class.java)
                _colleges.value = allColleges
                _error.value = null
            } catch (e: Exception) {
                Log.e("CollegesViewModel", "Error fetching colleges", e)
                _error.value = "Failed to load colleges. Please check your connection."
            } finally {
                _isLoading.value = false
            }
        }
    }

    // UPDATED to handle all new filters
    fun applyFilters(
        query: String,
        stream: String?,
        location: String?,
        state: String?,
        exam: String?,
        maxFees: Float
    ) {
        var filteredList = allColleges

        // Filter by search query (college name)
        if (query.isNotBlank()) {
            filteredList = filteredList.filter { college ->
                college.name?.contains(query, ignoreCase = true) == true
            }
        }

        // Filter by stream
        if (stream != null) {
            filteredList = filteredList.filter { college ->
                college.stream.equals(stream, ignoreCase = true)
            }
        }

        // Filter by location (city)
        if (location != null && location.isNotBlank()) {
            filteredList = filteredList.filter { college ->
                college.city?.contains(location, ignoreCase = true) == true
            }
        }

        // NEW: Filter by state
        if (state != null) {
            filteredList = filteredList.filter { college ->
                college.state.equals(state, ignoreCase = true)
            }
        }

        // NEW: Filter by entrance exam
        if (exam != null) {
            filteredList = filteredList.filter { college ->
                college.requiredExams?.any { it.equals(exam, ignoreCase = true) } == true
            }
        }

        // Filter by max fees (budget)
        if (maxFees != -1f) {
            val maxFeesInRupees = maxFees * 100_000
            filteredList = filteredList.filter { college ->
                val currentFees = college.fees
                currentFees != null && currentFees <= maxFeesInRupees
            }
        }

        _colleges.value = filteredList
    }
}