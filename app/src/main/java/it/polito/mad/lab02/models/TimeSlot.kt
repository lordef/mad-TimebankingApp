package it.polito.mad.lab02.models

data class TimeSlot(
    val id: String, //only local
    val title: String,
    val description: String,
    val dateTime: String,
    val duration: String,
    val location: String,
    val skill: String,
    val user: String,
    val userProfile: Profile,
    val assignee: String, //reference to assignee (user)
    val state: String, //AVAILABLE, ACCEPTED
    val pendingRequests: List<String> //reference to requesters (user)
)