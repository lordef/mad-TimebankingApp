package it.polito.mad.lab02.viewmodels

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.models.Skill
import it.polito.mad.lab02.models.TimeSlot
import java.lang.ref.Reference
import java.sql.Time

class PublicTimeSlotListViewModel(application: Application) : AndroidViewModel(application) {

    private val _timeSlotList = MutableLiveData<List<TimeSlot>>()


    //LiveData passed to our fragment
    val timeslotList: LiveData<List<TimeSlot>> = _timeSlotList


    //Creation of a Firebase db instance
    private var l: ListenerRegistration
    private var l1: ListenerRegistration
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // TODO: filtrare adv passati
    init {
        l = db.collection("timeslots").addSnapshotListener { r, e ->
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
                        val tmpTimeslot = TimeSlot(it.id, it.title, it.description, it.dateTime, it.duration, it.location, it.skill, it.user, tmpProfile!!)
                        tmpList.add(tmpTimeslot)
                    }
                }
                _timeSlotList.value = tmpList
            }
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
            val datetime = get("dateTime") as String //TODO valutare tipo per le date
            val duration = get("duration") as String // TODO time in milliseconds
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

    override fun onCleared() {
        super.onCleared()
        l.remove()
        l1.remove()
    }
}