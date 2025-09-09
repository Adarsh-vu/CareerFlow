package com.arshcreations.careerflow.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class College(
    val id: Long = 0,
    val avgPackage: Double = 0.0,
    val city: String = "",
    val description: String = "",
    val fees: Double = 0.0,
    val images: List<String> = emptyList(),
    val name: String = "",
    val nirfRanking: Long = 0,
    val state: String = "",
    val stream: String = "",
    val type: String = "",
    val requiredExams: List<String> = emptyList(),
    val closingRank: Long = 0,
    val admissionStart: String = "",
    val admissionEnd: String = ""
) : Parcelable