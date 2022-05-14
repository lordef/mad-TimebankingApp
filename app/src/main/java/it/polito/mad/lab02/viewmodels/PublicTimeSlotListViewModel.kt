package it.polito.mad.lab02.viewmodels

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import it.polito.mad.lab02.models.Skill
import it.polito.mad.lab02.models.TimeSlot
import java.lang.ref.Reference

class PublicTimeSlotListViewModel(application: Application) : AndroidViewModel(application) {

    private val _timeSlotList = MutableLiveData<List<TimeSlot>>()


    //LiveData passed to our fragment
    val timeslotList: LiveData<List<TimeSlot>> = _timeSlotList


    //Creation of a Firebase db instance
    private var l: ListenerRegistration
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    // TODO: filtrare adv passati
    init {
        l = db.collection("timeslots").addSnapshotListener { r, e ->
            _timeSlotList.value = if (e != null)
                emptyList()
            else r!!.mapNotNull { d ->
                d.toTimeslot()
            }
        }
    }

    private fun DocumentSnapshot.toTimeslot(): TimeSlot? {
        return try {
            val title = get("title") as String
            val description = get("description") as String
            val datetime = get("dateTime") as String //TODO valutare tipo per le date
            val duration = get("duration") as String // TODO time in milliseconds
            val location = get("location") as String
            val skill = get("skill") as DocumentReference

            TimeSlot(
                this.id,
                title,
                description,
                datetime,
                duration,
                location,
                skill.path
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }


    fun filterByTitle(title: String){
        db.collection("timeslots")
            .whereEqualTo("title", title)
            .get()
            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    _timeSlotList.value = emptyList()
//                    Log.d("Mytag", "${document.id} => ${document.data}")
//                }
                _timeSlotList.value = documents.mapNotNull { d ->
                    Log.d("MYTAG", "${d.id} => ${d.data}")
                    d.toTimeslot()
                }
            }
            .addOnFailureListener { exception ->
                _timeSlotList.value = emptyList()
                Log.w("MYTAG", "Error getting documents: ", exception)
            }
    }

    //TODO: EXAMPLES from
    // https://cloud.google.com/firestore/docs/query-data/queries
    private fun simpleQueries() {
        // [START simple_queries]
        // Create a reference to the cities collection
        val citiesRef = db.collection("cities")

        // Create a query against the collection.
        val query = citiesRef.whereEqualTo("state", "CA")
        // [END simple_queries]

        // [START simple_query_capital]
        val capitalCities = db.collection("cities").whereEqualTo("capital", true)
        // [END simple_query_capital]

        // [START example_filters]
        val stateQuery = citiesRef.whereEqualTo("state", "CA")
        val populationQuery = citiesRef.whereLessThan("population", 100000)
        val nameQuery = citiesRef.whereGreaterThanOrEqualTo("name", "San Francisco")
        // [END example_filters]

        // [START simple_query_not_equal]
        val notCapitalQuery = citiesRef.whereNotEqualTo("capital", false)
        // [END simple_query_not_equal]
    }


    override fun onCleared() {
        super.onCleared()
        l.remove()
    }
}