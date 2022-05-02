package it.polito.mad.lab02

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.models.TimeSlot
import it.polito.mad.lab02.models.TimeSlotList
import java.io.File

//TODO: change name in SharedPreferences
class SharedPreference(context: Context) {

    private val PREFERENCE_NAME = "SharedPreference"
    private val PREFERENCE_PROFILE = "profile"

    /* Retrieve our sort of DB instance (- a simple file) */
    private val sharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)


    //Retrieve profile info if present
    fun getProfile(): Profile {
        val defaultProfileImage = "android.resource://it.polito.mad.lab02/drawable/profile_image"
        val defaultProfile = Profile(
            imageUri = defaultProfileImage,
            fullName = "",
            nickname = "",
            email = "",
            location = "",
            skills = "",
            description = ""
        )

        val gson = Gson()
        val defaultProfileJson = gson.toJson(defaultProfile)
        val profileJson = sharedPreferences.getString(PREFERENCE_PROFILE, defaultProfileJson)
        val profile = Gson().fromJson(profileJson, Profile::class.java)
        return profile
    }

    fun setProfile(profile: Profile) {
        val editor = sharedPreferences.edit()
        //profile must be a string to put it in preferences
        // TODO: test if is sufficient method toString()
        //  it should be converted to a JSON first
        val profileJson = Gson().toJson(profile)
        editor.putString(PREFERENCE_PROFILE, profileJson)
        editor.apply()
    }


    //Retrieve list of timeslots if present
    fun getTimeSlots(): MutableList<TimeSlot>? {
        val timeSlotListTemp = mutableListOf<TimeSlot>()
        val defaultTimeSlotListDetails = TimeSlotList(
            listOf()
        )
        val defaultTimeSlotListJson = Gson().toJson(defaultTimeSlotListDetails)
        val timeSlotListJson = sharedPreferences.getString("TimeSlotList", defaultTimeSlotListJson)
        val timeSlotList = Gson().fromJson(timeSlotListJson, TimeSlotList::class.java)
        for (id in timeSlotList.timeSlotIdList) {
            val timeSlotDetailsJson = sharedPreferences.getString("timeSlot_$id", "")
            if (timeSlotDetailsJson != "") {
                val timeSlotDetails = Gson().fromJson(timeSlotDetailsJson, TimeSlot::class.java)
                timeSlotListTemp.add(timeSlotDetails)
            }
        }
        return timeSlotListTemp
    }


    // get/set timeslot details
    fun getTimeSlot(id: String): TimeSlot? {
        val defaultTimeSlotDetails = TimeSlot(
            id = "dummy id",
            title = "dummy title",
            description = "this is a description",
            dateTime = "01/01/1900 00:00",
            duration = "dummy duration",
            location = "dummy location"
        )
        val defaultTimeSlotDetailsJson = Gson().toJson(defaultTimeSlotDetails)
        val timeSlotDetailsJson =
            sharedPreferences.getString("timeSlot_$id", defaultTimeSlotDetailsJson)
        val timeSlotDetails = Gson().fromJson(timeSlotDetailsJson, TimeSlot::class.java)
        return timeSlotDetails
    }

    fun setTimeSlot(timeslot: TimeSlot, edit: Boolean) {
        val editor = sharedPreferences.edit()
        //timeslot must be a string to put it in preferences
        // TODO: test if is sufficient method toString()
        //  it should be converted to a JSON first

        //only if not in edit mode, but create mode
        if(!edit){
            val defaultTimeSlotListDetails = TimeSlotList(
                listOf()
            )
            val defaultTimeSlotListJson = Gson().toJson(defaultTimeSlotListDetails)
            var timeSlotListJson =
                sharedPreferences.getString("TimeSlotList", defaultTimeSlotListJson)
            val timeSlotList = Gson().fromJson(timeSlotListJson, TimeSlotList::class.java)

            val timeSlotListTemp = timeSlotList.timeSlotIdList.toMutableList()

            timeSlotListTemp.add(timeslot.id)
            timeSlotListJson = Gson().toJson(TimeSlotList(timeSlotListTemp.toList()))
            editor.putString("TimeSlotList", timeSlotListJson)
            editor.apply()
        }

        val timeslotJson = Gson().toJson(timeslot)
        editor.putString("timeSlot_${timeslot.id}", timeslotJson)
        editor.apply()

    }

    fun getMaxId(): String {
        val defaultTimeSlotListDetails = TimeSlotList(
            listOf()
        )
        val defaultTimeSlotListJson = Gson().toJson(defaultTimeSlotListDetails)
        var timeSlotListJson = sharedPreferences.getString("TimeSlotList", defaultTimeSlotListJson)
        val timeSlotList = Gson().fromJson(timeSlotListJson, TimeSlotList::class.java)
        val timeSlotListTemp = timeSlotList.timeSlotIdList.toMutableList()
        val last = timeSlotList.timeSlotIdList.lastOrNull()
        var max = 0
        if (last != null) {
            max = last.toInt() + 1
        }
        return max.toString()
    }


    //TODO: example of a Repo
    /*
    private val itemDao = ItemDatabase.getDatabase(application).itemDao()

    fun add(name: String) {
        val i = Item().also { it.name = name}
        itemDao.addItem(i)
    }

    fun sub(name: String) {
        itemDao.removeItemsWithName(name)
    }

    fun clear() {
        itemDao.removeAll()
    }

    fun count(): LiveData<Int> = itemDao.count()
    fun items(): LiveData<List<Item>> = itemDao.findAll()
    */
}