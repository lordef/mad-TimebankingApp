package it.polito.mad.lab02.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import it.polito.mad.lab02.SharedPreference
import it.polito.mad.lab02.models.TimeSlot

class TimeSlotDetailsViewModel(application: Application) : AndroidViewModel(application) {

    // We have not a DB => No repository
    // => we suppose that SharedPreferences is our persistence data layer
    //Try to retrieve data from shared preferences
    private val sharedPreferences = SharedPreference(application)

    private val timeSlotDetails = MutableLiveData<TimeSlot>()

    fun getTimeSlot(timeslotID: String): MutableLiveData<TimeSlot> {
        timeSlotDetails.also {
            it.value = sharedPreferences.getTimeSlot(timeslotID)
        }
        return timeSlotDetails
    }

    fun getMaxId(): String {
        return sharedPreferences.getMaxId()
    }

    fun updateTimeSlot(newTS: TimeSlot, isEdit: Boolean) {
        // Update persistence layer
        sharedPreferences.setTimeSlot(newTS, isEdit)

        // Update View Model
        timeSlotDetails.also {
            it.value = newTS
        }
    }
}