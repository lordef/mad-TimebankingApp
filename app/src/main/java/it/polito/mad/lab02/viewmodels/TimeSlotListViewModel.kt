package it.polito.mad.lab02.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.polito.mad.lab02.SharedPreference
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.models.TimeSlot
import it.polito.mad.lab02.models.TimeSlotList

class TimeSlotListViewModel(application: Application) : AndroidViewModel(application) {

    //Retrieve data from shared preferences
    private val sharedPreferences = SharedPreference(application)

    private val timeSlotList = MutableLiveData<MutableList<TimeSlot>>()

    fun getTimeSlotList(): MutableLiveData<MutableList<TimeSlot>> {
        timeSlotList.also {
            it.value = sharedPreferences.getTimeSlots()
        }
        return timeSlotList
    }

}