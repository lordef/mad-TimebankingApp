package it.polito.mad.lab02.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ListenerRegistration
import it.polito.mad.lab02.models.Skill

class SkillListViewModel(application: Application) : AndroidViewModel(application) {
    private val _skillList = MutableLiveData<List<Skill>>()
    private val _skill = MutableLiveData<Skill>()


    //LiveData passed to our fragment
    val skillList: LiveData<List<Skill>> = _skillList
    val skill = MutableLiveData<Skill>()


    //Creation of a Firebase db instance
    private var l: ListenerRegistration
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        l = db.collection("skills").addSnapshotListener { r, e ->
            _skillList.value = if (e != null)
                emptyList()
            else r!!.mapNotNull { d ->
                d.toSkill()
            }
        }
    }


    private fun DocumentSnapshot.toSkill(): Skill? {
        return try {
            val name = get("name") as String
            Skill(this.reference.toString(), name)
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


    /*
    override fun onCleared() {
        super.onCleared()
        l.remove()
    }
     */
}