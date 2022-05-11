package it.polito.mad.lab02.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.polito.mad.lab02.SharedPreference
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.models.TimeSlot

class ShowProfileViewModel(application: Application) : AndroidViewModel(application) {

    val sharedPreferences = SharedPreference(application)

    private val profileInfo = MutableLiveData<Profile>()

    fun getProfileInfo(): MutableLiveData<Profile> {
        profileInfo.also {
            it.value = sharedPreferences.getProfile()
        }
        return profileInfo
    }

    fun updateProfile(newP: Profile){
        sharedPreferences.setProfile(newP)

        profileInfo.also {
            it.value = newP
        }
    }

    fun addSkill(skill: String){
        profileInfo.also {
            it.value = Profile(
                it.value!!.imageUri,
                it.value!!.fullName,
                it.value!!.nickname,
                it.value!!.email,
                it.value!!.location,
                skill,
                it.value!!.description
            )
        }
    }
}