package it.polito.mad.lab02.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.models.Skill
import it.polito.mad.lab02.models.TimeSlot
import it.polito.mad.lab02.viewmodels.ViewmodelsUtils.toProfile
import it.polito.mad.lab02.viewmodels.ViewmodelsUtils.toSkill
import it.polito.mad.lab02.viewmodels.ViewmodelsUtils.toTimeslot
import java.util.*


class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val _timeSlotList = MutableLiveData<List<TimeSlot>>()
    private val _filteredTimeSlotList = MutableLiveData<List<TimeSlot>>()
    private val _profile = MutableLiveData<Profile>()
    private val _skillList = MutableLiveData<List<Skill>>()
    private val _loggedUserTimeSlotList = MutableLiveData<List<TimeSlot>>()


    //LiveData passed to our fragments
    val timeslotList: LiveData<List<TimeSlot>> = _timeSlotList
    val filteredTimeslotList: LiveData<List<TimeSlot>> = _filteredTimeSlotList
    val profile: LiveData<Profile> = _profile
    val skillList: LiveData<List<Skill>> = _skillList
    val loggedUserTimeSlotList: LiveData<List<TimeSlot>> = _loggedUserTimeSlotList


    //Creation of a Firebase db instance
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Create a reference to collections
    private val timeslotsRef = db.collection("timeslots")
    private val usersRef = db.collection("users")
    private val skillsRef = db.collection("skills")


    // Creating the ListenerRegistrations
    private lateinit var timeslotsListener: ListenerRegistration
    private lateinit var usersListener: ListenerRegistration
    var areTSsAndUsersListenersSetted = false

    private var loggedUserListener: ListenerRegistration
    private var skillsListener: ListenerRegistration

    private lateinit var loggedUserTimeSlotsListener: ListenerRegistration
    var isLoggedUserTSsListenerSetted = false


    init {
        // Creating listener for logged user
        loggedUserListener = usersRef
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)
            .addSnapshotListener { r, e ->
                _profile.value = if (e != null)
                    Profile("", "", "", "", "", emptyList(), "", "")
                else r!!.toProfile()
            }

        // Creating listener for all skills
        skillsListener = skillsRef
            .addSnapshotListener { r, e ->
                _skillList.value = if (e != null)
                    emptyList()
                else r!!.mapNotNull { d ->
                    d.toSkill()
                }
            }

    }


    /******** All timeslots ********/
    fun setPublicAdvsListenerBySkill(skillRefToString: String) {
        // Setting up timeslotsListener
        timeslotsListener = timeslotsRef
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

        usersListener = usersRef
            .addSnapshotListener { r, e ->
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
                areTSsAndUsersListenersSetted = true
            }
    }

    fun addFilter(filter: (TimeSlot) -> Boolean) {
        if (areTSsAndUsersListenersSetted) { //guarantees the initialization of the ListenerRegistration
            if (filter != null) {
                _filteredTimeSlotList.value = _timeSlotList.value?.filter(filter)
            } else {
                _filteredTimeSlotList.value = _timeSlotList.value
            }
        }//TODO: handle exception - no initialization of the ListenerRegistration


    }

    fun addOrder(order: String) {
        if (areTSsAndUsersListenersSetted) { //guarantees the initialization of the ListenerRegistration
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

    /******** end - All timeslots ********/


    /******** Logged User ********/
    fun updateProfile(newP: Profile) {
        usersRef
            .document(newP.uid)
            .update(
                "fullName", newP.fullName,
                "email", newP.email,
                "imageUri", newP.imageUri,
                "location", newP.location,
                "nickname", newP.nickname,
                "description", newP.description
            )

        _profile.also {
            it.value = newP
        }
    }

    fun deleteSkillFromLoggedUser(skill: String) {
        usersRef
            .document(_profile.value!!.uid)
            .update("skills", FieldValue.arrayRemove(skillsRef.document(skill)))
    }

    fun addSkillInLoggedUser(skill: String) {
        usersRef
            .document(_profile.value!!.uid)
            .update("skills", FieldValue.arrayUnion(skillsRef.document(skill)))
    }
    /******** end - Logged User ********/


    /******** Skills ********/

    fun addSkillInSkills(skill: String) {
        if (_skillList.value?.filter { it2 -> it2.name == skill }!!.isNotEmpty()) {
            val occurrences =
                _skillList.value?.filter { it2 -> it2.name == skill }?.get(0)?.occurrences
            if (occurrences != null) {
                skillsRef
                    .document(skill)
                    .update("occurrences", (occurrences.toInt() + 1) as Number)
            } else {

                val newSkill = mapOf(
                    "ref" to skillsRef.document(skill.toLowerCase()),
                    "name" to skill.toLowerCase(),
                    "occurrences" to 1 as Number
                )

                skillsRef
                    .document(skill)
                    .set(newSkill)
            }
        } else {
            val newSkill = mapOf(
                "ref" to skillsRef.document(skill.toLowerCase()),
                "name" to skill.toLowerCase(),
                "occurrences" to 1 as Number
            )

            skillsRef
                .document(skill)
                .set(newSkill)
        }
    }

    fun deleteSkillFromSkills(skill: String) {
        if (_skillList.value?.filter { it2 -> it2.name == skill }!!.isNotEmpty()) {
            val occurrences =
                _skillList.value?.filter { it2 -> it2.name == skill }?.get(0)?.occurrences
            if (occurrences!!.toInt() > 1) {
                skillsRef
                    .document(skill)
                    .update("occurrences", occurrences.toInt() - 1)
            } else {
                skillsRef
                    .document(skill)
                    .delete()
            }
        }
    }

    /******** end - Skills ********/


    /******** Logged user timeslots ********/

    fun setAdvsListenerByCurrentUser() {
        val userRef = db
            .collection("users")
            .document("${FirebaseAuth.getInstance().currentUser?.uid}")

        loggedUserTimeSlotsListener = timeslotsRef
            .whereEqualTo("user", userRef)
            .addSnapshotListener { r, e ->
                _loggedUserTimeSlotList.value = if (e != null)
                    emptyList()
                else r!!.mapNotNull { d ->
                    d.toTimeslot(profile = Profile("", "", "", "", "", emptyList(), "", ""))
                }
            }
            .also {
                isLoggedUserTSsListenerSetted = true
            }
    }

    fun updateTimeSlot(newTS: TimeSlot, isEdit: Boolean): String {
        var returnedId = ""

        if (isLoggedUserTSsListenerSetted) {
            val currentUser = usersRef
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
                val skillRef = skillsRef
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

            if (isEdit) { // edit
                timeslotsRef
                    .document(newTS.id)
                    .set(data)
            } else { // new
                val docReference = timeslotsRef.document()
                returnedId = docReference.id
                timeslotsRef
                    .document(docReference.id)
                    .set(data)
            }

        }
        return returnedId
    }


    fun deleteTimeSlot(timeslotId: String) {
        if(isLoggedUserTSsListenerSetted) {
            timeslotsRef.document(timeslotId).delete()
        }
    }

    /******** end - Logged user timeslots ********/


    override fun onCleared() {
        super.onCleared()
        if(areTSsAndUsersListenersSetted) {
            timeslotsListener.remove()
            usersListener.remove()
        }
        loggedUserListener.remove()
        skillsListener.remove()

        if (isLoggedUserTSsListenerSetted){
            loggedUserTimeSlotsListener.remove()
        }
    }

}