package it.polito.mad.lab02.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.models.TimeSlot

class TimeSlotListViewModel(application: Application) : AndroidViewModel(application) {

    private val _timeSlotList = MutableLiveData<List<TimeSlot>>()


    //LiveData passed to our fragment
    val timeslotList: LiveData<List<TimeSlot>> = _timeSlotList


    //Creation of a Firebase db instance
    private lateinit var l: ListenerRegistration
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun setAdvsListenerByCurrentUser(){
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
            val datetime = get("dateTime") as String
            val duration = get("duration") as String
            val location = get("location") as String
            val skill = get("skill")
            val user = get("user") as DocumentReference

            val skillTmp = if (skill == null) {
                ""
            } else {
                (skill as DocumentReference).path.split("/").last()
            }

            TimeSlot(
                this.id,
                title,
                description,
                datetime,
                duration,
                location,
                skillTmp,
                user.path,
                Profile("", "", "", "", "", emptyList(), "", "")
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    fun updateTimeSlot(newTS: TimeSlot, isEdit: Boolean): String {

        val currentUser = db.collection("users")
            .document("${FirebaseAuth.getInstance().currentUser?.uid}")

        val data: HashMap<String, Any>

        if (newTS.skill == "") {
            data = hashMapOf(
                "title" to newTS.title,
                "description" to newTS.description,
                "dateTime" to newTS.dateTime,
                "duration" to newTS.duration,
                "location" to newTS.location,
                "user" to currentUser
            )
        } else {
            val skillRef = db.collection("skills")
                .document(newTS.skill)
            data = hashMapOf(
                "title" to newTS.title,
                "description" to newTS.description,
                "dateTime" to newTS.dateTime,
                "duration" to newTS.duration,
                "location" to newTS.location,
                "skill" to skillRef,
                "user" to currentUser
            )
        }

        var id = ""

        if (isEdit) { // edit
            db
                .collection("timeslots")
                .document(newTS.id)
                .set(data)
        } else { // new
            val docReference = db.collection("timeslots").document()
            id = docReference.id
            db
                .collection("timeslots")
                .document(docReference.id)
                .set(data)
        }

        return id
    }

    fun deleteTimeSlot(timeslotId: String) {
        db.collection("timeslots").document(timeslotId).delete()
    }

    override fun onCleared() {
        super.onCleared()
        l.remove()
    }

}