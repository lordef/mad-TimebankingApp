package it.polito.mad.lab02.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import it.polito.mad.lab02.SharedPreference
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.models.TimeSlot
import it.polito.mad.lab02.models.TimeSlotList
import java.util.*

class TimeSlotListViewModel(application: Application) : AndroidViewModel(application) {

    private val _timeSlotList = MutableLiveData<List<TimeSlot>>()


    //LiveData passed to our fragment
    val timeslotList: LiveData<List<TimeSlot>> = _timeSlotList


    //Creation of a Firebase db instance
    private var l: ListenerRegistration
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        val userRef = db
            .collection("users")
            .document("${FirebaseAuth.getInstance().currentUser?.uid}")
        l = db.collection("timeslots")
            .whereEqualTo("user", userRef)
            .addSnapshotListener { r, e ->
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
            val datetime = get("dateTime") as Timestamp //TODO valutare tipo per le date
            val duration = get("duration") as Long // TODO time in milliseconds
            val location = get("location") as String
            val skill = get("skill") as DocumentReference

            TimeSlot(
                this.id,
                title,
                description,
                datetime.toString(),
                duration.toString(),
                location,
                skill.toString()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    fun updateTimeSlot(newTS: TimeSlot, b: Boolean): String {

        val currentUser = db.collection("users")
            .document("${FirebaseAuth.getInstance().currentUser?.uid}")

        var id = ""

        val data = hashMapOf(
            "title" to newTS.title,
            "description" to newTS.description,
            "dateTime" to newTS.dateTime,
            //"dateTime" to Timestamp(Date(newTS.dateTime)),
            "duration" to newTS.duration,
            "location" to newTS.location,
            "skill" to (newTS.skill as DocumentReference),
            "user" to currentUser
        )

        if(b){ // edit
            db
                .collection("timeslots")
                .document(newTS.id)
                .set(data)
        }
        else{ // new
            val docReference = db.collection("timeslots").document()
            id = docReference.id
            db
                .collection("timeslots")
                .add(data)
        }

        return id
    }

    override fun onCleared() {
        super.onCleared()
        l.remove()
    }

}