package it.polito.mad.lab02.models

data class Chat(
    val publisher: Profile,
    val requester: Profile,
    val timeSlot: TimeSlot,
    val id: String,
    val lastMessage: Message
)