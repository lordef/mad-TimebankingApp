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
import java.lang.reflect.TypeVariable
import java.sql.Time
import java.util.*

class PublicTimeSlotListViewModel(application: Application) : AndroidViewModel(application) {

    private val _timeSlotList = MutableLiveData<List<TimeSlot>>()
    private val _filteredTimeSlotList = MutableLiveData<List<TimeSlot>>()


    //LiveData passed to our fragment

    val timeslotList: LiveData<List<TimeSlot>> = _timeSlotList
    val filteredTimeslotList: LiveData<List<TimeSlot>> = _filteredTimeSlotList


    //Creation of a Firebase db instance
    private var l: ListenerRegistration
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Create a reference to the timeslot collection
    private val timeslotsRef = db.collection("timeslots")


    // TODO: filtrare adv passati
    init {
        l = timeslotsRef.addSnapshotListener { r, e ->
            _timeSlotList.value = if (e != null)
                emptyList()
            else r!!.mapNotNull { d ->
                d.toTimeslot()
            }
            addFilter{
                true
            }
            addOrder("datetime")
        }
    }

    fun addFilter(filter: (TimeSlot)->Boolean){
        if(filter != null){
            _filteredTimeSlotList.value = _timeSlotList.value?.filter(filter)
        }
        else{
            _filteredTimeSlotList.value = _timeSlotList.value
        }
    }

    fun addOrder(order: String){
        if(order != null){
            when(order){
                "datetime" -> _filteredTimeSlotList.value = _timeSlotList.value?.sortedWith(
                    compareBy<TimeSlot> { Date(it.dateTime).year }.thenBy { Date(it.dateTime).month }.thenBy { Date(it.dateTime).day }
                )
                "title" -> _filteredTimeSlotList.value = _timeSlotList.value?.sortedBy{
                    it.title
                }
                else -> _filteredTimeSlotList.value = _timeSlotList.value
            }

        }
        else{
            _filteredTimeSlotList.value = _timeSlotList.value
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

    //TODO: filters: can they be simultaneous?
    // title (OK),
    // description (NO),
    // datetime (TODO),
    // duration (TODO),
    // location (TODO),
    // utente (TODO: maybe difficult for reference of the user)
    // titolo, luogo, giorno, utente


    fun allTimeslots() {
        timeslotsRef
            .get()
            .addOnSuccessListener { documents ->
                _timeSlotList.value = documents.mapNotNull { d ->
                    Log.d("G07_TAG", "${d.id} => ${d.data}")
                    d.toTimeslot()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("G07_TAG", "Error getting documents: ", exception)
                _timeSlotList.value = emptyList()
            }
    }


    /**************************/
    //TODO
    //For a Full-text search
    // source: https://firebase.google.com/docs/firestore/solutions/search
    //for an "home made" solution:
    // source: https://medium.com/feedflood/filter-by-search-keyword-in-cloud-firestore-query-638377bf0123

    // Here, the title must be strictly equal (then also case sensitive)
    fun filterTimeslotsByTitle(title: String) {
        timeslotsRef
            .whereEqualTo("title", title)
            .get()
            .addOnSuccessListener { documents ->
                _timeSlotList.value = documents.mapNotNull { d ->
                    Log.d("G07_TAG", "${d.id} => ${d.data}")
                    d.toTimeslot()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("G07_TAG", "Error getting documents: ", exception)
                _timeSlotList.value = emptyList()
            }
    }
    /**************************/


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