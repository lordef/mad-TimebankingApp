package it.polito.mad.lab02.viewmodels

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import it.polito.mad.lab02.models.Skill
import it.polito.mad.lab02.models.TimeSlot

class PublicTimeSlotListViewModel(application: Application) : AndroidViewModel(application) {

    private val _timeSlotList = MutableLiveData<List<TimeSlot>>()
    private val _timeSlot = MutableLiveData<TimeSlot>()


    //LiveData passed to our fragment
    val skillList: LiveData<List<TimeSlot>> = _timeSlotList
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

    /*
    fun getProfileInfo(): MutableLiveData<Profile> {
        _profileInfo.also {
            it.value = sharedPreferences.getProfile()
        }
        return _profileInfo
    }

    fun updateProfile(newP: Profile) {
        sharedPreferences.setProfile(newP)

        _profileInfo.also {
            it.value = newP
        }
    }
    */


    //TODO: set returned value
    fun getTimeSlot(timeSlotID: String): MutableLiveData<TimeSlot> {
        // [START get_document]
        val docRef = db.collection("timeslots").document(timeSlotID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("MYTAG", "DocumentSnapshot data: ${document.data}")
                    _timeSlot.value = document.toTimeslot()
                } else {
                    Log.d("MYTAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }
        // [END get_document]
        return _timeSlot
    }

    fun getTimeSlotList(skill: String): MutableLiveData<List<TimeSlot>> {
        // [START get_document]
        val docRef = db.collection("skills").document(skill)
        val collRef = db.collection("timeslots").whereEqualTo("skill", docRef)
        collRef.get()
            .addOnSuccessListener { collection ->
                if (collection != null) {
                    Log.d("MYTAG", "DocumentSnapshot data: ${collection.documents}")
                    _timeSlotList.value = collection.toTimeSlotList()
                } else {
                    Log.d("MYTAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }
        // [END get_document]
        Log.d("myTag", "query al db")
        return _timeSlotList
    }

    private fun DocumentSnapshot.toTimeslot(): TimeSlot? {
        return try {
            val id = get("id") as Long
            val title = get("title") as String
            val description = get("description") as String
            val datetime = get("datetime") as Timestamp //TODO valutare tipo per le date
            val duration = get("duration") as Long // TODO time in milliseconds
            val location = get("location") as String

            TimeSlot(id.toString(), title, description, datetime.toString(), duration.toString(), location)
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
                listTmp.add(ts!!)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        return listTmp
    }
}