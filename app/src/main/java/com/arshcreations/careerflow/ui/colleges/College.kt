package com.arshcreations.careerflow.ui.colleges

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class College(
    @get:PropertyName("avgPackage") @set:PropertyName("avgPackage")
    var avgPackage: Double? = null,

    @get:PropertyName("city") @set:PropertyName("city")
    var city: String? = null,

    @get:PropertyName("description") @set:PropertyName("description")
    var description: String? = null,

    @get:PropertyName("fees") @set:PropertyName("fees")
    var fees: Double? = null,

    @get:PropertyName("images") @set:PropertyName("images")
    var images: List<String>? = null,

    @get:PropertyName("name") @set:PropertyName("name")
    var name: String? = null,

    @get:PropertyName("nirfRanking") @set:PropertyName("nirfRanking")
    var nirfRanking: Long? = null,

    @get:PropertyName("state") @set:PropertyName("state")
    var state: String? = null,

    @get:PropertyName("stream") @set:PropertyName("stream")
    var stream: String? = null,

    @get:PropertyName("type") @set:PropertyName("type")
    var type: String? = null
) : Parcelable {

    constructor() : this(null, null, null, null, null, null, null, null, null, null)
}