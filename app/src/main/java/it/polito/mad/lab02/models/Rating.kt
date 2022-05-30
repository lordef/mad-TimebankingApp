package it.polito.mad.lab02.models

data class Rating(
//    val ref: String, //it is its id -
    val rated: Profile, //reference
    val rater: Profile, //reference
    val starsNum: Int, //number
    val comment: String, //text
    val timestamp: String, //Timestamp
    val timeslot: TimeSlot

)