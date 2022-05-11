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
    private val _timeSlot = MutableLiveData<TimeSlot>()


    //LiveData passed to our fragment
    val timeslotList: LiveData<List<TimeSlot>> = _timeSlotList
    val skill = MutableLiveData<TimeSlot>()



    //Creation of a Firebase db instance
    private var l: ListenerRegistration
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

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
            val id = get("id") as Long
            val title = get("title") as String
            val description = get("description") as String
            val datetime = get("datetime") as Timestamp //TODO valutare tipo per le date
            val duration = get("duration") as Long // TODO time in milliseconds
            val location = get("location") as String
            val skill = get("skill") as DocumentReference

            TimeSlot(id.toString(), title, description, datetime.toString(), duration.toString(), location, skill.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    private fun QuerySnapshot.toTimeSlotList(): List<TimeSlot>? {
        val listTmp : MutableList<TimeSlot> = mutableListOf()
        for (s in this.documents){
            try {
                val ts = s.toTimeslot()

                Log.d("myTag", "Document: ${ts}")
                listTmp.add(ts!!)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        return listTmp
    }
}