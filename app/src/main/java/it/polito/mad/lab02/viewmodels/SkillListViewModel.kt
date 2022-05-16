package it.polito.mad.lab02.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ListenerRegistration
import it.polito.mad.lab02.models.Skill

class SkillListViewModel(application: Application) : AndroidViewModel(application) {
    private val _skillList = MutableLiveData<List<Skill>>()


    //LiveData passed to our fragment
    val skillList: LiveData<List<Skill>> = _skillList


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
            val occurrences = get("occurrences") as Number
            Skill(this.reference.path, name, occurrences)
//            Skill(this.reference.path, name)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    private fun QuerySnapshot.toSkillList(): List<Skill>? {
        val listTmp: MutableList<Skill> = mutableListOf()
        for (s in this.documents) {
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


    fun addSkill(skill: String) {
        if (_skillList.value?.filter { it2 -> it2.name == skill }!!.isNotEmpty()) {
            val occurrences =
                _skillList.value?.filter { it2 -> it2.name == skill }?.get(0)?.occurrences
            if (occurrences != null) {
                db.collection("skills")
                    .document(skill)
                    .update("occurrences", (occurrences.toInt() + 1) as Number)
            } else {
                val newSkill = mapOf(
                    "ref" to db.collection("skills").document(skill.toLowerCase()),
                    "name" to skill.toLowerCase(),
                    "occurrences" to 1 as Number
                )
                db.collection("skills")
                    .document(skill)
                    .set(newSkill)
            }
        } else {
            val newSkill = mapOf(
                "ref" to db.collection("skills").document(skill.toLowerCase()),
                "name" to skill.toLowerCase(),
                "occurrences" to 1 as Number
            )
            db.collection("skills")
                .document(skill)
                .set(newSkill)
        }
    }

    fun deleteSkill(skill: String) {
        if (_skillList.value?.filter { it2 -> it2.name == skill }!!.isNotEmpty()) {
            val occurrences =
                _skillList.value?.filter { it2 -> it2.name == skill }?.get(0)?.occurrences
            if (occurrences!!.toInt() > 1) {
                db.collection("skills")
                    .document(skill)
                    .update("occurrences", occurrences.toInt() - 1)
            } else {
                db.collection("skills")
                    .document(skill)
                    .delete()
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        l.remove()
    }

}