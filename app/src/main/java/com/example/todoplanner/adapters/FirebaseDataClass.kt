package com.example.todoplanner.adapters

data class FirebaseDataClass(
    val dateOfCreation : String? = "",
    val dateOfUpdater : String? = "",
    val description : String? = "",
    val expirationDate : String? = "",
    val header: String? = "",
    val priority: String? = ""
)