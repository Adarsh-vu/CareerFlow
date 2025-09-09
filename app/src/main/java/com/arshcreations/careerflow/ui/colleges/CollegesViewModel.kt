package com.arshcreations.careerflow.ui.colleges

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arshcreations.careerflow.data.models.College
import com.arshcreations.careerflow.data.models.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CollegesViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val collegesCollection = db.collection("colleges")

    private val _colleges = MutableLiveData<List<College>>()
    val colleges: LiveData<List<College>> = _colleges

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var currentFilters = ActiveFilters()
    private var searchJob: Job? = null

    fun getCurrentFilters(): ActiveFilters = currentFilters

    fun onQueryChanged(query: String) {
        currentFilters = currentFilters.copy(query = query)
        triggerSearch()
    }

    fun onFiltersApplied(filters: ActiveFilters) {
        currentFilters = currentFilters.copy(
            stream = filters.stream,
            state = filters.state,
            exam = filters.exam,
            maxFees = filters.maxFees
        )
        triggerSearch()
    }

    private fun triggerSearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300L) // Debounce delay
            fetchColleges(currentFilters)
        }
    }

    private suspend fun fetchColleges(filters: ActiveFilters) {
        _isLoading.postValue(true)
        try {
            var query: Query = collegesCollection
            filters.state?.let { query = query.whereEqualTo("state", it) }
            filters.stream?.let { query = query.whereEqualTo("stream", it) }
            filters.exam?.let { query = query.whereArrayContains("requiredExams", it) }

            if (filters.maxFees != -1f) {
                val maxFeesInRupees = filters.maxFees * 100_000
                query = query.whereLessThanOrEqualTo("fees", maxFeesInRupees)
            }

            var results = query.get().await().toObjects(College::class.java)

            if (filters.query.isNotBlank()) {
                results = results.filter { college ->
                    college.name.contains(filters.query, ignoreCase = true) ||
                            college.city.contains(filters.query, ignoreCase = true)
                }
            }
            _colleges.postValue(results)
            _error.postValue(null)

        } catch (e: Exception) {
            Log.e("CollegesViewModel", "Error applying filters", e)
            _error.postValue("Failed to load colleges. Please try again.")
        } finally {
            _isLoading.postValue(false)
        }
    }

    fun loadInitialData() {
        triggerSearch()
    }

    fun fetchCollegesBasedOnProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = auth.currentUser?.uid
            if (userId == null) {
                _error.value = "User not logged in."
                _isLoading.value = false
                return@launch
            }
            try {
                val document = db.collection("users").document(userId).get().await()
                val preferences = document.get("preferences", UserPreferences::class.java)

                if (preferences != null) {
                    val profileFilters = ActiveFilters(
                        state = preferences.locations.firstOrNull(),
                        stream = preferences.streams.firstOrNull(),
                        exam = preferences.attemptedExams.firstOrNull()
                    )

                    currentFilters = profileFilters
                    triggerSearch()
                } else {
                    _error.value = "Please set preferences to get recommendations."
                    _colleges.value = emptyList()
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Error fetching recommendations: ${e.message}"
                _isLoading.value = false
            }
        }
    }
}