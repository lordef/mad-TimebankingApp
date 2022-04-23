package it.polito.mad.lab02

import android.content.Context
import com.google.gson.Gson
import it.polito.mad.lab02.models.ProfileModel
import it.polito.mad.lab02.models.TimeSlotDetailsModel
import java.sql.Time

//TODO: change name in SharedPreferences
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

    fun setProfile(profile: ProfileModel){
        val editor = pref.edit()
        //profile must be a string to put it in preferences
        // TODO: test if is sufficient method toString()
        //  it should be converted to a JSON first
        val profileJson = Gson().toJson(profile)
        editor.putString(PREFERENCE_PROFILE, profileJson)
        editor.apply()
    }

    // get/set timeslot details
    fun getTimeSlotDetails(title: String) : TimeSlotDetailsModel? {

        val defaultTimeSlotDetails = TimeSlotDetailsModel(
            title = "new title - static obj from SharedPreferences",
            description = "new desc",
            dateTime = "new date and time",
            duration = "new duration",
            location = "new location"
        )
        val defaultTimeSlotDetailsJson = Gson().toJson(defaultTimeSlotDetails)
        val timeSlotDetailsJson = pref.getString(title, defaultTimeSlotDetailsJson)
        val timeSlotDetails = Gson().fromJson(timeSlotDetailsJson, TimeSlotDetailsModel::class.java)
        return timeSlotDetails
    }

    fun setTimeSlotDetails(title: String, timeslot: TimeSlotDetailsModel){
        val editor = pref.edit()
        //timeslot must be a string to put it in preferences
        // TODO: test if is sufficient method toString()
        //  it should be converted to a JSON first
        val timeslotJson = Gson().toJson(timeslot)
        editor.putString(title, timeslotJson)
        editor.apply()
    }

}