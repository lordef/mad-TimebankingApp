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


    fun addSkill(names: List<String>){
        names.forEach { it1 ->

            var oldVal : Number
            if(skillList.value?.filter { it2 -> it2.name == it1 }!!.isEmpty()) oldVal = 0
            else oldVal = (skillList.value?.filter { it2 -> it2.name == it1 }?.get(0)?.occurrences!!)



            if (oldVal != 0) {
                db.collection("skills")
                    .document(it1)
                    .update("occurrences", (oldVal.toInt()+1) as Number )
            }
            else if(it1 != ""){
                val newSkill = Skill("skills/"+it1, it1, 1)
                db.collection("skills")
                    .document(it1)
                    .set(newSkill)
            }

        }
    }
    fun deleteSkill(names: List<String>){
        names.forEach { it1 ->
            var oldVal : Number
            if(skillList.value?.filter { it2 -> it2.name == it1 }!!.isEmpty()) oldVal = 0
            else oldVal = (skillList.value?.filter { it2 -> it2.name == it1 }?.get(0)?.occurrences!!)




            if (oldVal != 1 && oldVal != null) {
                db.collection("skills")
                    .document(it1)
                    .update("occurrences", oldVal.toInt()-1 )
            }
            else if(it1 != ""){
                db.collection("skills")
                    .document(it1)
                    .delete()
            }

        }
    }


    override fun onCleared() {
        super.onCleared()
        l.remove()
    }

}