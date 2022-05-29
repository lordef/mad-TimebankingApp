package it.polito.mad.lab02.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import it.polito.mad.lab02.fragments.communication.MessageRecyclerViewAdapter
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.models.Rating
import it.polito.mad.lab02.models.Skill
import it.polito.mad.lab02.models.TimeSlot
import it.polito.mad.lab02.models.*
import it.polito.mad.lab02.viewmodels.ViewmodelsUtils.toChat
import it.polito.mad.lab02.viewmodels.ViewmodelsUtils.toMessage
import it.polito.mad.lab02.viewmodels.ViewmodelsUtils.toNotification
import it.polito.mad.lab02.viewmodels.ViewmodelsUtils.toProfile
import it.polito.mad.lab02.viewmodels.ViewmodelsUtils.toRating
import it.polito.mad.lab02.viewmodels.ViewmodelsUtils.toSkill
import it.polito.mad.lab02.viewmodels.ViewmodelsUtils.toStarsNumber
import it.polito.mad.lab02.viewmodels.ViewmodelsUtils.toTimeslot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.HashMap


class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val _timeSlotList = MutableLiveData<List<TimeSlot>>()
    private val _filteredTimeSlotList = MutableLiveData<List<TimeSlot>>()
    private val _profile = MutableLiveData<Profile>()
    private val _skillList = MutableLiveData<List<Skill>>()
    private val _loggedUserTimeSlotList = MutableLiveData<List<TimeSlot>>()
    private val _ratingNumber = MutableLiveData<Float>()
    private val _ratingList = MutableLiveData<List<Rating>>()
    private val _myAssignedTimeSlotList = MutableLiveData<List<TimeSlot>>()


    private val _isChatListenerSet = MutableLiveData<Boolean>(false)

    private val _timeSlot = MutableLiveData<TimeSlot?>()


    private val _publisherChatList = MutableLiveData<List<Chat>>()
    private val _requesterChatList = MutableLiveData<List<Chat>>()
    private val _messageList = MutableLiveData<List<Message>>()
    private val _newMessage = MutableLiveData<Notification?>()


    //LiveData passed to our fragments
    val timeslotList: LiveData<List<TimeSlot>> = _timeSlotList
    val filteredTimeslotList: LiveData<List<TimeSlot>> = _filteredTimeSlotList
    val profile: LiveData<Profile> = _profile
    val skillList: LiveData<List<Skill>> = _skillList
    val loggedUserTimeSlotList: LiveData<List<TimeSlot>> = _loggedUserTimeSlotList
    val ratingNumber: LiveData<Float> = _ratingNumber
    val ratingList: LiveData<List<Rating>> = _ratingList
    val publisherChatList: LiveData<List<Chat>> = _publisherChatList
    val requesterChatList: LiveData<List<Chat>> = _requesterChatList
    val messageList: LiveData<List<Message>> = _messageList
    val newMessage: LiveData<Notification?> = _newMessage
    val myAssignedTimeSlotList: LiveData<List<TimeSlot>> = _myAssignedTimeSlotList

    val isChatListenerSet: LiveData<Boolean> = _isChatListenerSet

    val timeSlot: LiveData<TimeSlot?> = _timeSlot

    //Creation of a Firebase db instance
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Create a reference to collections
    private val timeslotsRef = db.collection("timeslots")
    private val usersRef = db.collection("users")
    private val skillsRef = db.collection("skills")
    private val ratingsRef = db.collection("ratings")
    private val chatsRef = db.collection("chats")


    // Creating the ListenerRegistrations
    private lateinit var timeslotsListener: ListenerRegistration
    private lateinit var usersListener: ListenerRegistration
    var areTSsAndUsersListenersSetted = false

    private lateinit var myAssignedTimeSlotListListener: ListenerRegistration

    private var loggedUserListener: ListenerRegistration
    private var skillsListener: ListenerRegistration

    private lateinit var loggedUserTimeSlotsListener: ListenerRegistration
    var isLoggedUserTSsListenerSetted = false

    private lateinit var ratingNumbersListener: ListenerRegistration
    private var isRatingNumbersListenerSetted = false

    private lateinit var ratingsListener: ListenerRegistration
    private var isRatingsListenerSetted = false

    private lateinit var publisherChatsListener: ListenerRegistration
    private lateinit var requesterChatsListener: ListenerRegistration
    var isChatsListenerSetted = false

    var isRequesterChatsListenerSet = false

    private lateinit var messagesListener: ListenerRegistration

    private lateinit var timeslotListener: ListenerRegistration

    private lateinit var newMessageListener: ListenerRegistration
    var isNewMessageListenerSet = false

    init {
        setNewMessageListener()
        // Creating listener for logged user
        loggedUserListener = usersRef
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)
            .addSnapshotListener { r, e ->
                _profile.value = if (e != null)
                    Profile("", "", "", "", "", emptyList(), "", "", 0)
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

    /******** new Messages notification ********/

    fun setNewMessageListener() {
        newMessageListener = db.collectionGroup("messages")
            .addSnapshotListener { r, e ->
                if (e != null)
                    _newMessage.value = null
                else {
                    if (r != null) {

                        viewModelScope.launch(Dispatchers.IO) {
                            if (r.documentChanges.isNotEmpty()) {
                                val dc = r.documentChanges.last()
                                val user = (dc.document.get("user") as DocumentReference)
                                    .get().await().toProfile()
                                val user1 = (dc.document.get("user1") as DocumentReference)
                                    .get().await().toProfile()
                                if (user1?.uid == FirebaseAuth.getInstance().currentUser?.uid) {
                                    val chat = chatsRef
                                        .document(dc.document.reference.parent.parent!!.id)
                                        .get().await()
                                    val publisher = (chat.get("publisher") as DocumentReference).id
                                    var timeslot: TimeSlot?
                                    if (publisher == user?.uid) {
                                        timeslot = (chat
                                            .get("timeslot") as DocumentReference).get().await()
                                            .toTimeslot(user)
                                    } else {
                                        timeslot = (chat
                                            .get("timeslot") as DocumentReference).get().await()
                                            .toTimeslot(user1)
                                    }

                                    dc.document.toNotification(user, user1, timeslot)?.let {
                                        if (isNewMessageListenerSet) {
                                            if (_newMessage.value != null) {
                                                if (it.timestamp > _newMessage.value!!.timestamp) {
                                                    _newMessage.postValue(it)
                                                }
                                            } else {
                                                _newMessage.postValue(it)
                                            }
                                        }
                                    }

                                }
                            }
                            isNewMessageListenerSet = true
                        }
                    }
                }
            }
    }

    /******** end new Messages notification ********/

    /******** Chats and Messages ********/
    fun setChatsListener() {
        // Setting up timeslotsListener

        publisherChatsListener = chatsRef
            .whereEqualTo(
                "publisher",
                usersRef.document(FirebaseAuth.getInstance().currentUser?.uid!!)
            )
            .addSnapshotListener { r, e ->
                if (e != null)
                    _publisherChatList.value = emptyList()
                else {
                    val tmpList = mutableListOf<Chat>()

                    viewModelScope.launch(Dispatchers.IO) {
                        r!!.forEach { d ->
                            val requester = (d.get("requester") as DocumentReference)
                                .get().await().toProfile()
                            val publisher = (d.get("publisher") as DocumentReference)
                                .get().await().toProfile()
                            val timeSlot = (d.get("timeslot") as DocumentReference)
                                .get().await().toTimeslot(publisher)

                            val lastMessageMap = (d.get("lastMessage") as HashMap<String, Any>)
                            val lastMessage: Message?

                            val user = (lastMessageMap["user"] as DocumentReference)
                                .get().await().toProfile()
                            val user1 = (lastMessageMap["user1"] as DocumentReference)
                                .get().await().toProfile()
                            lastMessage = lastMessageMap.toMessage(user, user1)
                            if (lastMessage != null) {
                                d.toChat(requester, publisher, timeSlot, lastMessage)
                                    ?.let { tmpList.add(it) }
                            }
                        }
                        _publisherChatList.postValue(tmpList)
                    }
                }
            }

        requesterChatsListener = chatsRef
            .whereEqualTo(
                "requester",
                usersRef.document(FirebaseAuth.getInstance().currentUser?.uid!!)
            )
            .addSnapshotListener { r, e ->
                if (e != null)
                    _requesterChatList.value = emptyList()
                else {
                    val tmpList = mutableListOf<Chat>()

                    viewModelScope.launch(Dispatchers.IO) {
                        r!!.forEach { d ->
                            val requester = (d.get("requester") as DocumentReference)
                                .get().await().toProfile()
                            val publisher = (d.get("publisher") as DocumentReference)
                                .get().await().toProfile()
                            val timeSlot = (d.get("timeslot") as DocumentReference)
                                .get().await().toTimeslot(publisher)

                            val lastMessageMap = (d.get("lastMessage") as HashMap<String, Any>)
                            val lastMessage: Message?

                            val user = (lastMessageMap["user"] as DocumentReference)
                                .get().await().toProfile()
                            val user1 = (lastMessageMap["user1"] as DocumentReference)
                                .get().await().toProfile()
                            lastMessage = lastMessageMap.toMessage(user, user1)
                            if (lastMessage != null) {
                                d.toChat(requester, publisher, timeSlot, lastMessage)
                                    ?.let { tmpList.add(it) }
                            }
                        }
                        _requesterChatList.postValue(tmpList)
                    }
                }
            }.also {
                isChatsListenerSetted = true
                _isChatListenerSet.value = true
            }
    }

    fun setRequesterChatsListener() {
        requesterChatsListener = chatsRef
            .whereEqualTo(
                "requester",
                usersRef.document(FirebaseAuth.getInstance().currentUser?.uid!!)
            )
            .addSnapshotListener { r, e ->
                if (e != null)
                    _requesterChatList.value = emptyList()
                else {
                    val tmpList = mutableListOf<Chat>()

                    viewModelScope.launch(Dispatchers.IO) {
                        r!!.forEach { d ->
                            val requester = (d.get("requester") as DocumentReference)
                                .get().await().toProfile()
                            val publisher = (d.get("publisher") as DocumentReference)
                                .get().await().toProfile()
                            val timeSlot = (d.get("timeslot") as DocumentReference)
                                .get().await().toTimeslot(publisher)

                            val lastMessageMap = (d.get("lastMessage") as HashMap<String, Any>)
                            val lastMessage: Message?

                            val user = (lastMessageMap["user"] as DocumentReference)
                                .get().await().toProfile()
                            val user1 = (lastMessageMap["user1"] as DocumentReference)
                                .get().await().toProfile()
                            lastMessage = lastMessageMap.toMessage(user, user1)
                            if (lastMessage != null) {
                                d.toChat(requester, publisher, timeSlot, lastMessage)
                                    ?.let { tmpList.add(it) }
                            }
                        }
                        _requesterChatList.postValue(tmpList)
                    }
                }
            }.also {
                isRequesterChatsListenerSet = true
            }
    }

    fun removeRequesterChatsListener() {
        if (isRequesterChatsListenerSet) {
            isRequesterChatsListenerSet = false
            requesterChatsListener.remove()

            _requesterChatList.value = emptyList()
        }
    }


    fun setMessagesListener(chatId: String) {
        messagesListener = chatsRef.document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { r, e ->
                if (e != null)
                    _messageList.value = emptyList()
                else {
                    val tmpList = mutableListOf<Message>()

                    viewModelScope.launch(Dispatchers.IO) {
                        r!!.forEach { d ->
                            val user = (d.get("user") as DocumentReference)
                                .get().await().toProfile()
                            val user1 = (d.get("user1") as DocumentReference)
                                .get().await().toProfile()
                            d.toMessage(user, user1)?.let { tmpList.add(it) }
                        }
                        _messageList.postValue(tmpList)
                    }
                }
            }.also {
                isChatsListenerSetted = true
            }
    }

    /******** end - Chats and Messages ********/

    /******** Single timeslot ********/

    fun setTimeSlotListener(ts: TimeSlot) {
        timeslotListener = timeslotsRef
            .document(ts.id)
            .addSnapshotListener { r, e ->
                if (e != null)
                    _timeSlot.value = null
                else {
                    if (r != null) {
                        _timeSlot.value = r.toTimeslot(ts.userProfile)
                    }
                }
            }
    }

    fun setTimeSlotState(s: String, ts: TimeSlot) {
        timeslotsRef
            .document(ts.id)
            .update("state", s)
    }

    fun setTimeSlotAssignee(a: String, ts: TimeSlot, payer: Profile): Boolean {
        val durationTmp = ts.duration.split(":")[0].toInt() * 60 + ts.duration.split(":")[1].toInt()
        var outcome = false
        if (payer.balance - durationTmp >= 0) {
            runBlocking {
                db.runTransaction { transaction ->
                    transaction.update(
                        timeslotsRef.document(ts.id),
                        "assignee",
                        usersRef.document(a)
                    )
                    transaction
                        .update(
                            usersRef.document(payer.uid),
                            "balance",
                            (payer.balance - durationTmp) as Number
                        )
                    transaction
                        .update(
                            usersRef.document(FirebaseAuth.getInstance().currentUser!!.uid),
                            "balance", FieldValue.increment(durationTmp.toLong())
                        )
                    transaction
                        .update(
                            timeslotsRef
                                .document(ts.id), "state", "ACCEPTED"
                        )
                    outcome = true
                }.await()
            }
        }
        return outcome
    }

    fun setTimeSlotRequest(a: String, ts: TimeSlot) {
        timeslotsRef
            .document(ts.id)
            .update("pendingRequests", FieldValue.arrayUnion(usersRef.document(a)))
    }

    fun removeTimeSlotRequest(a: String, ts: TimeSlot) {
        timeslotsRef
            .document(ts.id)
            .update("pendingRequests", FieldValue.arrayRemove(usersRef.document(a)))
    }

    /******** end - Single timeslot ********/


    /******** All timeslots ********/

    fun setMyAssignedTimeSlotListListener() {
        myAssignedTimeSlotListListener = timeslotsRef
            .whereEqualTo(
                "assignee",
                usersRef.document(FirebaseAuth.getInstance().currentUser!!.uid)
            )
            .whereEqualTo("state", "ACCEPTED")
            .addSnapshotListener { r, e ->
                if (e != null)
                    _myAssignedTimeSlotList.value = emptyList()
                else {
                    val tmpList = mutableListOf<TimeSlot>()

                    viewModelScope.launch(Dispatchers.IO) {
                        r!!.forEach { d ->
                            val userProfile = (d.get("user") as DocumentReference)
                                .get().await().toProfile()

                            d.toTimeslot(userProfile)?.let { tmpList.add(it) }
                        }
                        _myAssignedTimeSlotList.postValue(tmpList)
                    }
                }
            }
    }

    fun setPublicAdvsListenerBySkill(skillRefToString: String) {
        // Setting up timeslotsListener
        timeslotsListener = timeslotsRef
            .whereEqualTo("skill", db.document(skillRefToString))
            .whereEqualTo("state", "AVAILABLE")
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
                            it.user == d.reference.id
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
                                tmpProfile!!,
                                it.assignee,
                                it.state,
                                it.pendingRequests
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

    fun removePublicAdvsListener() {
        if (areTSsAndUsersListenersSetted) {
            areTSsAndUsersListenersSetted = false
            timeslotsListener.remove()
            usersListener.remove()

            _timeSlotList.value = emptyList()
        }
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
                    "id" to skillsRef.document(skill.toLowerCase()),
                    "name" to skill.toLowerCase(),
                    "occurrences" to 1 as Number
                )

                skillsRef
                    .document(skill)
                    .set(newSkill)
            }
        } else {
            val newSkill = mapOf(
                "id" to skillsRef.document(skill.toLowerCase()),
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
        val userRef = usersRef
            .document("${FirebaseAuth.getInstance().currentUser?.uid}")

        loggedUserTimeSlotsListener = timeslotsRef
            .whereEqualTo("user", userRef)
            .addSnapshotListener { r, e ->
                _loggedUserTimeSlotList.value = if (e != null)
                    emptyList()
                else r!!.mapNotNull { d ->
                    d.toTimeslot(Profile("", "", "", "", "", emptyList(), "", "", 0))
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
                    "user" to currentUser,
                    "assignee" to usersRef.document(newTS.assignee),
                    "state" to newTS.state,
                    "pendingRequests" to newTS.pendingRequests.map { usersRef.document(it) }
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
                    "user" to currentUser,
                    "assignee" to usersRef.document(newTS.assignee),
                    "state" to newTS.state,
                    "pendingRequests" to newTS.pendingRequests.map { usersRef.document(it) }
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
        if (isLoggedUserTSsListenerSetted) {
            timeslotsRef.document(timeslotId).delete()
        }
    }

    fun removeAdvsListenerByCurrentUser() {
        if (isLoggedUserTSsListenerSetted) {
            isLoggedUserTSsListenerSetted = false

            loggedUserTimeSlotsListener.remove()

            _loggedUserTimeSlotList.value = emptyList()
        }
    }


    /******** end - Logged user timeslots ********/

    /******** Chat functionalities ********/

    fun getChat(ts: TimeSlot): String? {
        var exists = false
        var chat: QuerySnapshot
        runBlocking {
            chat = chatsRef
                .whereEqualTo("publisher", usersRef.document(ts.user))
                .whereEqualTo(
                    "requester",
                    usersRef.document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                )
                .whereEqualTo("timeslot", timeslotsRef.document(ts.id))
                .get().await()
            if (chat.documents.size >= 1) {
                exists = true
            }
        }
        if (exists) {
            return chat.documents.first().id
        } else return null
    }

    fun createChat(ts: TimeSlot): String {
        var exists = false
        var chat: QuerySnapshot
        runBlocking {
            chat = chatsRef
                .whereEqualTo("publisher", usersRef.document(ts.user))
                .whereEqualTo(
                    "requester",
                    usersRef.document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                )
                .whereEqualTo("timeslot", timeslotsRef.document(ts.id))
                .get().await()
            if (chat.documents.size >= 1) {
                exists = true
            }
        }

        if (exists) {
            return chat.documents.first().id
        }

        val newChat = chatsRef.document()
        val data = hashMapOf(
            "publisher" to usersRef.document(ts.user),
            "requester" to usersRef.document(FirebaseAuth.getInstance().currentUser?.uid.toString()),
            "timeslot" to timeslotsRef.document(ts.id)
        )
        newChat.set(data)
        return newChat.id
    }

    fun sendMessage(chatId: String, message: String, user1: String): Boolean {
        return if (message != "") {
            val data = hashMapOf(
                "text" to message,
                "timestamp" to Timestamp(Calendar.getInstance().time),
                "user" to usersRef.document(FirebaseAuth.getInstance().currentUser?.uid.toString()),
                "user1" to usersRef.document(user1)
            )

            if (_messageList.value == null || _messageList.value?.isEmpty() == true) {
                chatsRef.document(chatId).collection("messages").document("1").set(data)
                setMessagesListener(chatId)
                chatsRef.document(chatId).update("lastMessage", data)
                _isChatListenerSet.value = true
            } else {
                _messageList.value?.last()
                    ?.let {
                        chatsRef.document(chatId).collection("messages")
                            .document((it.id.toInt() + 1).toString()).set(data)
                        chatsRef.document(chatId).update("lastMessage", data)
                    }
            }

            true
        } else {
            false
        }
    }

    fun clearChat() {
        _messageList.value = emptyList()
    }

    /******** end - Chat functionalities ********/

    /******** Ratings ********/

    fun setRatingNumberListenerByUserUid(ratedProfileUid: String) {
        val userRef = usersRef
            .document(ratedProfileUid)

        ratingNumbersListener = ratingsRef
            .whereEqualTo("rated", userRef)
            .addSnapshotListener { r, e ->
                if (e != null)
                    _ratingNumber.value = 0f
                else {
                    val tmpStarNumsList = mutableListOf<Int>()
                    r!!.forEach { d ->
                        d.toStarsNumber()?.let {
                            tmpStarNumsList.add(it)
                        }
                    }
                    if (tmpStarNumsList.isEmpty()) //No detected ratings for this user
                        _ratingNumber.value = 0f
                    else //Average of ratings for this user
                        _ratingNumber.value = tmpStarNumsList.average().toFloat()
                }
            }
            .also {
                isRatingNumbersListenerSetted = true
            }
    }

    fun setRatingsListenerByUserUid(ratedProfileUid: String) {
        val userRef = usersRef
            .document(ratedProfileUid)

        ratingsListener = ratingsRef
            .whereEqualTo("rated", userRef)
            .addSnapshotListener { r, e ->
                _ratingList.value = if (e != null)
                    emptyList()
                else r!!.mapNotNull { d ->
                    d.toRating()
                }
            }
            .also {
                isRatingsListenerSetted = true
            }
    }

    fun removeRatingNumberListener() {
        if (isRatingNumbersListenerSetted) {
            isRatingNumbersListenerSetted = false

            ratingNumbersListener.remove()

            _ratingNumber.value = 0f
        }
    }

    fun removeRatingsListener() {
        if (isRatingsListenerSetted) {
            isRatingsListenerSetted = false

            ratingsListener.remove()

            _ratingList.value = emptyList()
        }
    }


    /******** end - Ratings ********/

    override fun onCleared() {
        super.onCleared()
        if (areTSsAndUsersListenersSetted) {
            timeslotsListener.remove()
            usersListener.remove()
        }

        loggedUserListener.remove()
        skillsListener.remove()

        if (isLoggedUserTSsListenerSetted) {
            loggedUserTimeSlotsListener.remove()
        }

        if (isRatingNumbersListenerSetted) {
            ratingNumbersListener.remove()
        }

        if (isRatingsListenerSetted) {
            ratingsListener.remove()
        }
    }


}


/*
    Resource documents:
    - https://firebase.google.com/docs/firestore/query-data/queries#kotlin+ktx
    - https://github.com/firebase/snippets-android/blob/efcc7a3a4d0ceb37475dbb6bc3a5c008d0363134/firestore/app/src/main/java/com/google/example/firestore/kotlin/DocSnippets.kt#L842-L844
*/