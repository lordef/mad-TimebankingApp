package it.polito.mad.lab02.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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
                    Profile("", "", "", "", "", "", "", "")
                else r!!.toProfile()
                Log.d("MYTAG", "My profile: ${_profile.value}")
            }
    }

    fun updateProfile(newP: Profile) {
        db.collection("users")
            .document(newP.uid)
            .set(newP)

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
            val skills = get("skills") as String
            val description = get("description") as String
            val uid = get("uid") as String

            Profile(imageUri, fullName, nickname, email, location, skills, description, uid)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    override fun onCleared() {
        super.onCleared()
        l.remove()
    }

    fun addSkill(skill: String){
        _profile.also {
            it.value = Profile(
                it.value!!.imageUri,
                it.value!!.fullName,
                it.value!!.nickname,
                it.value!!.email,
                it.value!!.location,
                skill,
                it.value!!.description,
                it.value!!.uid
            )
        }
    }
}