package it.polito.mad.lab02

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson

class SharedPreference(context : Context){

    private val PREFERENCE_NAME = "SharedPreference"
    private val PREFERENCE_PROFILE = "profile"
    val profileImage = Uri.parse("android.resource://it.polito.mad.lab02/drawable/profile_image")

    private val obj = ProfileClass(
        imageUri = profileImage,
        fullName = "Full name",
        nickname = "Nickname",
        email = "Email",
        location = "Location",
        skills = "Skills",
        description = "Description"
    )
    private val gson = Gson()
    private val json = gson.toJson(obj)

    private val pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun getProfile() : String? {
        return pref.getString(PREFERENCE_PROFILE, json)
    }

    fun setProfile(profile: String){
        val editor = pref.edit()
        editor.putString(PREFERENCE_PROFILE, profile)
        editor.apply()
    }
}