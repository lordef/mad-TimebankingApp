package it.polito.mad.lab02.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimeSlotListViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Advertisement Fragment"
    }
    val text: LiveData<String> = _text
}