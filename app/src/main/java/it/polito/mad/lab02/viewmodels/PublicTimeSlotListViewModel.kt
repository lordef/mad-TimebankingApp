package it.polito.mad.lab02.viewmodels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import it.polito.mad.lab02.models.Skill

class PublicTimeSlotListViewModel {

    private val _timeSlotList = MutableLiveData<List<Skill>>()
    private val _timeSlot = MutableLiveData<Skill>()


    //LiveData passed to our fragment
    val skillList: LiveData<List<Skill>> = _timeSlotList
    val skill = MutableLiveData<Skill>()


    //Creation of a Firebase db instance
    private var l: ListenerRegistration
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        l = db.collection("skills").addSnapshotListener { r, e ->
            _timeSlotList.value = if (e != null)
                emptyList()
            else r!!.mapNotNull { d ->
                d.toSkill()
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
    fun getSkill(skillID: String): MutableLiveData<Skill> {
        // [START get_document]
        val docRef = db.collection("skills").document(skillID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("MYTAG", "DocumentSnapshot data: ${document.data}")
                    _timeSlot.value = document.toSkill()
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

    fun getSkillList(): MutableLiveData<List<Skill>> {
        // [START get_document]
        val collRef = db.collection("skills")
        collRef.get()
            .addOnSuccessListener { collection ->
                Log.d("MYTAG", "Collection empty?: ${collection}")
                if (collection != null) {
                    Log.d("MYTAG", "DocumentSnapshot data: ${collection.documents}")
                    _timeSlotList.value = collection.toSkillList()
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

    private fun DocumentSnapshot.toSkill(): Skill? {
        return try {
            val name = get("name") as String

            Skill(name)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    private fun QuerySnapshot.toSkillList(): List<Skill>? {
        val listTmp : MutableList<Skill> = mutableListOf()
        for (s in this.documents){
            try {
                val skill = s.toSkill()
                listTmp.add(skill!!)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        return listTmp
    }
}