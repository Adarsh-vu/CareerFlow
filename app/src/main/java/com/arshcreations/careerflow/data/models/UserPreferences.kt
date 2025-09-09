package com.arshcreations.careerflow.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserPreferences(
    val dob: String = "",
    val attemptedExams: List<String> = emptyList(),
    val marks: Double? = null,
    val rank: Int? = null,
    val locations: List<String> = emptyList(),
    val streams: List<String> = emptyList(),
    val annualIncome: Long? = null,
    val stability: Int = 0
) : Parcelable