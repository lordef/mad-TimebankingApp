package it.polito.mad.lab02.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import it.polito.mad.lab02.SharedPreference
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.models.TimeSlot

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    /*****************************/
    /* TODO: remove older and useless code */
    private val _text = MutableLiveData<String>().apply {
        value = "This is profile Fragment"
    }
    val text: LiveData<String> = _text

    /*****************************/

    //Retrieve data from shared preferences
    //TODO: test if application works as context for preferences
    private val sharedPreferences = SharedPreference(application)

    private val profile = MutableLiveData<Profile>()

    fun getProfile(): MutableLiveData<Profile> {
        profile.also {
            it.value = sharedPreferences.getProfile()
        }
        return profile
    }

    //TODO: update method for single field or like this?
    fun updateProfile(newProfile: Profile){
        // Update View Model
        profile.value = newProfile

        // Update persistence layer
        sharedPreferences.setProfile(newProfile)

        //TODO: update the persistence layer -> is thread useful?
        /*
        thread {
            repo.add("item${value.value}")
        }
        */
    }


}