package it.polito.mad.lab02

import android.content.Context
import com.google.gson.Gson
import it.polito.mad.lab02.models.ProfileModel
import it.polito.mad.lab02.models.TimeSlotDetailsModel

class SharedPreference(context : Context){

    private val PREFERENCE_NAME = "SharedPreference"
    private val PREFERENCE_PROFILE = "profile"

    private val pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    // get/set profile
    fun getProfile() : String? {
        val profileImage = "android.resource://it.polito.mad.lab02/drawable/profile_image"

        val obj = ProfileModel(
            imageUri = profileImage,
            fullName = "",
            nickname = "",
            email = "",
            location = "",
            skills = "",
            description = ""
        )
        val gson = Gson()
        val json = gson.toJson(obj)
        return pref.getString(PREFERENCE_PROFILE, json)
    }

    fun setProfile(profile: String){
        val editor = pref.edit()
        editor.putString(PREFERENCE_PROFILE, profile)
        editor.apply()
    }

    // get/set timeslot details
    fun getTimeSlotDetails(title: String) : String? {

        val obj = TimeSlotDetailsModel(
            title = "new title",
            description = "new desc",
            dateTime = "new date and time",
            duration = "new duration",
            location = "new location"
        )
        val gson = Gson()
        val json = gson.toJson(obj)
        return pref.getString(title, json)
    }

    fun setTimeSlotDetails(title: String, timeslot: String){
        val editor = pref.edit()
        editor.putString(title, timeslot)
        editor.apply()
    }

}