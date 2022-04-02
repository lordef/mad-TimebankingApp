package it.polito.mad.lab02

import android.net.Uri

data class ProfileClass(val imageUri: Uri,
                        val fullName: String,
                        val nickname: String,
                        val email: String,
                        val location: String,
                        val skills: String,
                        val description: String)
