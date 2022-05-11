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
import com.google.firebase.firestore.QuerySnapshot
import it.polito.mad.lab02.SharedPreference
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.models.Skill

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
                Log.d("MYTAG", "Profile: ${_profile.value}")
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


    val sharedPreferences = SharedPreference(application)

    private val profileInfo = MutableLiveData<Profile>()

    fun getProfileInfo(): MutableLiveData<Profile> {
        profileInfo.also {
            it.value = sharedPreferences.getProfile()
        }
        return profileInfo
    }

    fun updateProfile(newP: Profile) {
        sharedPreferences.setProfile(newP)

        profileInfo.also {
            it.value = newP
        }
    }

    fun addSkill(skill: String) {
        profileInfo.also {
            it.value = Profile(
                it.value!!.imageUri,
                it.value!!.fullName,
                it.value!!.nickname,
                it.value!!.email,
                it.value!!.location,
                skill,
                it.value!!.description,
                "" //TODO
            )
        }
    }
}