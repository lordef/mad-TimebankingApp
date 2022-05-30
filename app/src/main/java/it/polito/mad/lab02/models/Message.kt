package it.polito.mad.lab02.models

import com.google.firebase.Timestamp

data class Message(
    val text: String,
    val timestamp: Timestamp,
    val user: Profile,
    val user1: Profile,
    val system: Boolean,
    val id: String
)