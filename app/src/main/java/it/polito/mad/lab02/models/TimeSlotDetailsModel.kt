package it.polito.mad.lab02.models

data class TimeSlotDetailsModel(
    val title: String,
    val description: String,
    val dateTime: String,
    val duration: String,
    val location: String
)