package it.polito.mad.lab02.models

data class Profile(
    val imageUri: String,
    val fullName: String,
    val nickname: String,
    val email: String,
    val location: String,
    val skills: String,
    val description: String,
    val uid: String
)
