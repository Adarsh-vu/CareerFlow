package com.arshcreations.careerflow.ui.colleges

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ActiveFilters(
    val query: String = "",
    val stream: String? = null,
    val state: String? = null,
    val exam: String? = null,
    val maxFees: Float = -1f
) : Parcelable