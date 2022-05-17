package it.polito.mad.lab02.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import it.polito.mad.lab02.models.Profile

class ShowProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val _profile = MutableLiveData<Profile>()


    //LiveData passed to our fragment
    val profile: LiveData<Profile> = _profile


    //Creation of a Firebase db instance
    private var l: ListenerRegistration
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        l = db.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)
            .addSnapshotListener { r, e ->
                _profile.value = if (e != null)
                    Profile("", "", "", "", "", emptyList(), "", "")
                else r!!.toProfile()
            }
    }

    fun updateProfile(newP: Profile) {
        db.collection("users")
            .document(newP.uid)
            .update("fullName", newP.fullName,
            "email", newP.email,
            "imageUri", newP.imageUri,
            "location", newP.location,
            "nickname", newP.nickname,
            "description", newP.description)

        _profile.also {
            it.value = newP
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

            Profile(
                imageUri,
                fullName,
                nickname,
                email,
                location,
                tmpList,
                description,
                uid
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

    fun deleteSkill(skill: String) {
        db.collection("users")
            .document(_profile.value!!.uid)
            .update("skills", FieldValue.arrayRemove(db.collection("skills").document(skill)))
    }

    fun addSkill(skill: String) {
        db.collection("users")
            .document(_profile.value!!.uid)
            .update("skills", FieldValue.arrayUnion(db.collection("skills").document(skill)))
    }
}