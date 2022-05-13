package it.polito.mad.lab02.viewmodels

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import it.polito.mad.lab02.models.Skill
import it.polito.mad.lab02.models.TimeSlot
import java.lang.ref.Reference

class PublicTimeSlotListViewModel(application: Application) : AndroidViewModel(application) {

    private val _timeSlotList = MutableLiveData<List<TimeSlot>>()


    //LiveData passed to our fragment
    val timeslotList: LiveData<List<TimeSlot>> = _timeSlotList


    //Creation of a Firebase db instance
    private var l: ListenerRegistration
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    // TODO: filtrare adv passati
    init {
        l = db.collection("timeslots").addSnapshotListener { r, e ->
            _timeSlotList.value = if (e != null)
                emptyList()
            else r!!.mapNotNull { d ->
                d.toTimeslot()
            }
        }
    }

    private fun DocumentSnapshot.toTimeslot(): TimeSlot? {
        return try {
            val title = get("title") as String
            val description = get("description") as String
            val datetime = get("dateTime") as String //TODO valutare tipo per le date
            val duration = get("duration") as String // TODO time in milliseconds
            val location = get("location") as String
            val skill = get("skill") as DocumentReference

            TimeSlot(
                this.id,
                title,
                description,
                datetime,
                duration,
                location,
                skill.path
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    override fun onCleared() {
        super.onCleared()
        l.remove()
    }
}