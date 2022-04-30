package it.polito.mad.lab02.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.polito.mad.lab02.SharedPreference
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.models.TimeSlot

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    val sharedPreferences = SharedPreference(application)

    private val profileInfo = MutableLiveData<Profile>()

    fun getProfileInfo(): MutableLiveData<Profile> {
        profileInfo.also {
            it.value = sharedPreferences.getProfile()
        }
        return profileInfo
    }

    //TODO: update method for single field?
    fun updateTimeSlot(title: String, newP: Profile){
        profileInfo.value = newP

        sharedPreferences.setProfile(newP)

        //TODO: update the persistence layer -> is thread useful?
        /*
        thread {
            repo.add("item${value.value}")
        }
        */
    }

}