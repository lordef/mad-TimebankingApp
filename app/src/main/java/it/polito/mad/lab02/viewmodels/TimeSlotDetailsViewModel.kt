package it.polito.mad.lab02.viewmodels

import android.app.Application
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import it.polito.mad.lab02.R
import it.polito.mad.lab02.SharedPreference
import it.polito.mad.lab02.models.TimeSlotDetailsModel
import kotlin.concurrent.thread

//TODO: work in progress
class TimeSlotDetailsViewModel(application: Application): AndroidViewModel(application) {

    // We have not a DB => No repository
    // => we suppose that SharedPreferences is our persistence data layer
    //Try to retrieve data from shared preferences
    //TODO: test if application works as context for preferences
    val sharedPreferences = SharedPreference(application)

    private val timeSlotDetailsModel = MutableLiveData<TimeSlotDetailsModel>()
        .also {
            it.value = sharedPreferences.getTimeSlotDetails("placeholder_title") //TODO
        }

    fun getTimeSlotDetails(): MutableLiveData<TimeSlotDetailsModel> {
        return timeSlotDetailsModel
    }

    //TODO: update method for single field?
    fun update(newObj: TimeSlotDetailsModel){
        timeSlotDetailsModel.value = newObj

        //TODO: update the persistence layer
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