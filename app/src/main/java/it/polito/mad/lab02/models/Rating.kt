package it.polito.mad.lab02.models

data class Rating (
    val rated: String,
    val rater: String,
    val stars: Int,
    val comment: String,
    val timestamp: String
)