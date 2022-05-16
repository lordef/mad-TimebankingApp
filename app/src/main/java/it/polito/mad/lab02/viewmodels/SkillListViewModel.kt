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
            val occurrences = get("occurrences") as Int
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
        // TODO: come si fa a vedere skillList come con l'observer qui???
        Log.d("mytagg", skillList.value.toString())
        names.forEach { it1 ->
            val oldVal = skillList.value?.filter { it2 -> it2.name == it1 }?.get(0)?.occurrences
            Log.d("mytagg", skillList.value.toString())



            if (oldVal != null) {
                db.collection("skills")
                    .document(it1)
                    .update("occurrences", oldVal+1 )
//                _skillList.also { it.value?.filter { it2 -> it2.name == it1 }?.get(0)?.occurrences =
//                    it.value?.filter { it2 -> it2.name == it1 }?.get(0)?.occurrences?.plus(
//                        1
//                    )!!
//                }
            }
            else{
                val newSkill = Skill("skills/"+it1, it1, 1)
                db.collection("skills")
                    .document(it1)
                    .set(newSkill)
            }

        }
    }
    fun deleteSkill(names: List<String>){
        Log.d("mytagg", skillList.value.toString())
        names.forEach { it1 ->
            val oldVal = skillList.value?.filter { it2 -> it2.name == it1 }?.get(0)?.occurrences
            Log.d("mytagg", skillList.value.toString())



            if (oldVal != 1 && oldVal != null) {
                db.collection("skills")
                    .document(it1)
                    .update("occurrences", oldVal-1 )
//                _skillList.also { it.value?.filter { it2 -> it2.name == it1 }?.get(0)?.occurrences =
//                    it.value?.filter { it2 -> it2.name == it1 }?.get(0)?.occurrences?.plus(
//                        1
//                    )!!
//                }
            }
            else{
                val newSkill = Skill("skills/"+it1, it1, 1)
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