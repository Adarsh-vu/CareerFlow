package com.arshcreations.careerflow.data.models

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val preferences: UserPreferences? = null
)