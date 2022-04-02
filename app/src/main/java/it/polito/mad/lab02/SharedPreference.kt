package it.polito.mad.lab02

import android.content.Context
import com.google.gson.Gson

class SharedPreference(context : Context){

    val PREFERENCE_NAME = "SharedPreference"
    val PEFERENCE_POFILE = "profile"

    val obj = ProfileClass(
        fullName = "Full name",
        nickname = "Nickname",
        email = "Email",
        location = "Location",
        skills = "Skills",
        description = "Description"
    )
    val gson = Gson()
    val json = gson.toJson(obj)

    val pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun getProfile() : String? {
        return pref.getString(PEFERENCE_POFILE, json)
    }

    fun setProfile(profile: String){
        val editor = pref.edit()
        editor.putString(PEFERENCE_POFILE, profile)
        editor.apply()
    }
}