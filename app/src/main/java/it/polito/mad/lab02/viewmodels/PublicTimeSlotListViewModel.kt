package it.polito.mad.lab02.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.models.TimeSlot
import java.util.*


class PublicTimeSlotListViewModel(application: Application) : AndroidViewModel(application) {

    private val _timeSlotList = MutableLiveData<List<TimeSlot>>()
    private val _filteredTimeSlotList = MutableLiveData<List<TimeSlot>>()


    //LiveData passed to our fragment

    val timeslotList: LiveData<List<TimeSlot>> = _timeSlotList
    val filteredTimeslotList: LiveData<List<TimeSlot>> = _filteredTimeSlotList


    //Creation of a Firebase db instance
    private lateinit var l: ListenerRegistration
    private lateinit var l1: ListenerRegistration
    var areListenerRegistrationsSetted = false
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()


    // Create a reference to the timeslot collection
    private val timeslotsRef = db.collection("timeslots")


    fun setPublicAdvsListenerBySkill(skillRefToString: String) {

        l = db.collection("timeslots")
            .whereEqualTo("skill", db.document(skillRefToString))
            .addSnapshotListener { r, e ->
                if (e != null)
                    _timeSlotList.value = emptyList()
                else {
                    val tmpList = mutableListOf<TimeSlot>()

                    r!!.forEach { d ->
                        (d.get("user") as DocumentReference)
                            .get().addOnSuccessListener {
                                val profile = it.toProfile()
                                if (profile != null) {
                                    val ts = d.toTimeslot(profile)
                                    if (ts != null) {
                                        tmpList.add(ts)
                                        _timeSlotList.value = tmpList
                                    }
                                }
                            }
                    }
                }
            }

        l1 = db.collection("users").addSnapshotListener { r, e ->
            if (e != null) {

            } else {
                val tmpList = mutableListOf<TimeSlot>()
                r!!.forEach { d ->
                    val tmpProfile = d.toProfile()
                    _timeSlotList.value?.filter {
                        it.user == d.reference.path
                    }?.forEach {
                        val tmpTimeslot = TimeSlot(
                            it.id,
                            it.title,
                            it.description,
                            it.dateTime,
                            it.duration,
                            it.location,
                            it.skill,
                            it.user,
                            tmpProfile!!
                        )
                        tmpList.add(tmpTimeslot)
                    }
                }
                _timeSlotList.value = tmpList
            }
        }.also {
            areListenerRegistrationsSetted = true
        }
    }

    private fun DocumentSnapshot.toProfile(): Profile? {
        return try {
            val imageUri = get("imageUri") as String
            val fullName = get("fullName") as String
            val nickname = get("nickname") as String
            val email = get("email") as String
            val location = get("location") as String
            val skills = get("skills") as List<DocumentReference>
            val description = get("description") as String
            val uid = get("uid") as String
            val tmpList = skills.map { s -> s.path.split("/").last() }

            Profile(imageUri, fullName, nickname, email, location, tmpList, description, uid)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    private fun DocumentSnapshot.toTimeslot(profile: Profile): TimeSlot? {
        return try {
            val title = get("title") as String
            val description = get("description") as String
            val datetime = get("dateTime") as String
            val duration = get("duration") as String
            val location = get("location") as String
            val skill = get("skill") as DocumentReference
            val user = get("user") as DocumentReference

            TimeSlot(
                this.id,
                title,
                description,
                datetime,
                duration,
                location,
                skill.path,
                user.path,
                profile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    fun addFilter(filter: (TimeSlot) -> Boolean) {
        if (areListenerRegistrationsSetted) { //guarantees the initialization of the ListenerRegistration
            if (filter != null) {
                _filteredTimeSlotList.value = _timeSlotList.value?.filter(filter)
            } else {
                _filteredTimeSlotList.value = _timeSlotList.value
            }
        }//TODO: handle exception - no initialization of the ListenerRegistration


    }

    fun addOrder(order: String) {
        if (areListenerRegistrationsSetted) { //guarantees the initialization of the ListenerRegistration
            if (order != null && _timeSlotList.value?.size!! >= 2) {
                when (order) {

                    "datetime" -> _filteredTimeSlotList.value =
                        _timeSlotList.value?.sortedBy { Timestamp(Date(it.dateTime)).seconds }
                    "datetime_desc" -> _filteredTimeSlotList.value =
                        _timeSlotList.value?.sortedByDescending { Timestamp(Date(it.dateTime)).seconds }

                    "title" -> _filteredTimeSlotList.value =
                        _timeSlotList.value?.sortedBy { it.title }
                    "title_desc" -> _filteredTimeSlotList.value =
                        _timeSlotList.value?.sortedByDescending { it.title }

                    else -> _filteredTimeSlotList.value = _timeSlotList.value
                }

            } else {
                _filteredTimeSlotList.value = _timeSlotList.value
            }
        }//TODO: handle exception - no initialization of the ListenerRegistration

    }

    override fun onCleared() {
        super.onCleared()
        l.remove()
        l1.remove()
    }
}