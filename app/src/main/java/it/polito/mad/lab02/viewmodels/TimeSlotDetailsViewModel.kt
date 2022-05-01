package it.polito.mad.lab02.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import it.polito.mad.lab02.SharedPreference
import it.polito.mad.lab02.models.TimeSlot

//TODO: work in progress
class TimeSlotDetailsViewModel(application: Application): AndroidViewModel(application) {

    // We have not a DB => No repository
    // => we suppose that SharedPreferences is our persistence data layer
    //Try to retrieve data from shared preferences
    //TODO: test if application works as context for preferences
    private val sharedPreferences = SharedPreference(application)

    private val timeSlotDetails = MutableLiveData<TimeSlot>()

    fun getTimeSlot(timeslotID : String): MutableLiveData<TimeSlot> {
        timeSlotDetails.also {
            it.value = sharedPreferences.getTimeSlot(timeslotID)
        }
        return timeSlotDetails
    }

    fun getMaxId(): String{
        return sharedPreferences.getMaxId()
    }

    //TODO: update method for single field?
    fun updateTimeSlot(newTS: TimeSlot, b: Boolean){
        // Update View Model
        timeSlotDetails.value = newTS

        // Update persistence layer
        sharedPreferences.setTimeSlot(newTS, b)

        //TODO: update the persistence layer -> is thread useful?
        /*
        thread {
            repo.add("item${value.value}")
        }
        */
    }

    /* Code from lecture on a08-fragments */
    /*
    private val counter = MutableLiveData<Int>().also { it.value = 0 }

    fun add() {
        counter.value = (counter.value?:0)+1
    }
     */
}