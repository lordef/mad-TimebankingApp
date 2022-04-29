package it.polito.mad.lab02.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimeSlotListViewModel(application: Application) : AndroidViewModel(application) {
    /*****************************/
    /* TODO: remove older and useless code */
    private val _text = MutableLiveData<String>().apply {
        value = "This is Advertisement Fragment"
    }
    val text: LiveData<String> = _text
    /*****************************/

    //TODO: interaction with shared preferences


}