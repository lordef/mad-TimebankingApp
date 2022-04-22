package it.polito.mad.lab02.viewmodels

import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import it.polito.mad.lab02.R
import it.polito.mad.lab02.SharedPreference
import it.polito.mad.lab02.models.TimeSlotDetailsModel
import kotlin.concurrent.thread

//TODO: work in progress
class TimeSlotDetailsViewModel: ViewModel() {

    // We have not a DB => No repository
    //Try to retrieve data from shared preferences
    /*
    val pref = this.context?.let { SharedPreference(it) }
    val gson = Gson()
    val json = pref?.getTimeSlotDetails(title)
    if (!json.equals("")) {
        val obj = gson.fromJson(json, TimeSlotDetailsModel::class.java)
        // Put it into the TextViews
        val title = view.findViewById<TextView>(R.id.titleTextView)
        val description = view.findViewById<TextView>(R.id.descriptionTextView)
        val dateTime = view.findViewById<TextView>(R.id.dateTimeTextView)
        val duration = view.findViewById<TextView>(R.id.durationTextView)
        val location = view.findViewById<TextView>(R.id.locationTextView)

        if (obj !== null) {
            title.text = obj.title
            description.text = obj.description
            dateTime.text = obj.dateTime
            duration.text = obj.duration
            location.text = obj.location
        }
    }*/

    //----
    // Temp data to create a TimeSlotDetailsModel
    val obj = TimeSlotDetailsModel(
        title = "new title FROM VM",
        description = "new desc",
        dateTime = "new date and time",
        duration = "new duration",
        location = "new location"
    )
    //----

    //    val value: LiveData<Int> = repo.count()
    //    val items: LiveData<List<Item>> = repo.items()
    private val timeSlotDetailsModel = MutableLiveData<TimeSlotDetailsModel>()
        .also { it.value = obj }


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