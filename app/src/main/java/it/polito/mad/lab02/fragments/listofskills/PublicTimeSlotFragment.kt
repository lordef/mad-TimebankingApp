package it.polito.mad.lab02.fragments.listofskills

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.forEach
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import it.polito.mad.lab02.R
import it.polito.mad.lab02.viewmodels.PublicTimeSlotListViewModel

/**
 * A fragment representing a list of Items.
 */
class PublicTimeSlotFragment : Fragment(R.layout.fragment_public_time_slot_list_filter_sort) {

    private var columnCount = 1

    private val vm by activityViewModels<PublicTimeSlotListViewModel>()

    private var actualFilter: AdvsFilter = AdvsFilter.ALL
    private var filteredTitle: String = ""

    var filter = "No filter"
    var sort = "No sorting"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val recyclerView = view.findViewById<RecyclerView>(R.id.public_time_slot_list)
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)

        val skill = arguments?.getString("skill")
        if (skill != null) {
            if (skill != null) {
                (activity as AppCompatActivity?)?.supportActionBar?.title =
                    "Skill: " + skill.split("/").last()
                Log.d("MYTAG", "Passed skill: ${skill}")
                vm.timeslotList.observe(viewLifecycleOwner) {

                    val allFilterButton = view.findViewById<Button>(R.id.sortButton)
                    allFilterButton.setOnClickListener {
                        actualFilter = AdvsFilter.ALL
//                    vm.allTimeslots()
                        vm.addFilter {
                            true
                        }
                    }


                    val timeSlotList = it.filter { ts ->
                        Log.d("MYTAG", "Reference skill: ${ts.skill}")
                        ts.skill == skill
                    }
                    Log.d("MYTAG", "Doc ref: $timeSlotList")

                    Log.d("MYTAGGGG", "list: ${timeSlotList}")

                    val myAdapter = PublicTimeSlotRecyclerViewAdapter(timeSlotList)
                    if (recyclerView is RecyclerView) {
                        with(recyclerView) {
                            layoutManager = when {
                                columnCount <= 1 -> LinearLayoutManager(context)
                                else -> GridLayoutManager(context, columnCount)
                            }
                            adapter = myAdapter
                        }
                    }

                    val filterButton = view.findViewById<Button>(R.id.filterButton)
                    filterButton.setOnClickListener {
//                    vm.filterByTitle("Test 1")
//                        // from text view for example
//                        actualFilter = AdvsFilter.TITLE
//                        filteredTitle = "Test 1" //TODO: here text from text view


//                        vm.addFilter {
//                            it.title.contains("test", ignoreCase = true)
//                        }
                        showFilterMenu(filterButton, R.menu.filter_criteria_menu, myAdapter)

                    }

                    val sortButton = view.findViewById<Button>(R.id.sortButton)
                    sortButton.setOnClickListener {
//                    vm.filterByTitle("Test 1")
//                        // from text view for example
//                        actualFilter = AdvsFilter.TITLE
//                        filteredTitle = "Test 1" //TODO: here text from text view


//                        vm.addFilter {
//                            it.title.contains("test", ignoreCase = true)
//                        }
                        showSortMenu(sortButton, R.menu.sort_criteria_menu, myAdapter)

                    }


                    val textView = view.findViewById<TextView>(R.id.text_pub_advertisements)
                    if (timeSlotList.isEmpty()) {
                        recyclerView.visibility = View.GONE
                        textView.visibility = View.VISIBLE
                    } else {
                        textView.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    }
                }
            }

            val callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    view.findNavController().navigateUp()
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(callback)
        }
    }

    private fun showSortMenu(
        v: View,
        @MenuRes menuRes: Int,
        adapter: PublicTimeSlotRecyclerViewAdapter
    ) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.menu.forEach {
            if (it.title == sort) {
                it.isChecked = true
            }
        }

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            menuItem.isChecked = true

            if (menuItem.title == "No sorting") {
                adapter.setOrder("No sorting")
                sort = "No sorting"

            }
            if (menuItem.title == "Title") {
                if (sort == "Title") {
                    adapter.setOrder("Title_desc")
                } else {
                    adapter.setOrder("Title")
                }
                sort = "Title"
            }
            if (menuItem.title == "Location") {
                if (sort == "Location") {
                    adapter.setOrder("Location")
                } else {
                    adapter.setOrder("Location")
                }
                sort = "Location"
            }
            if (menuItem.title == "Duration") {
                if (sort == "Duration") {
                    adapter.setOrder("Duration_desc")
                } else {
                    adapter.setOrder("Duration")
                }
                sort = "Duration"
            }
            if (menuItem.title == "Date and Time") {
                if (sort == "Date and Time") {
                    adapter.setOrder("Date and Time_desc")
                } else {
                    adapter.setOrder("Date and Time")
                }
                sort = "Date and Time"
            }

            true
        }

        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }

    private fun showFilterMenu(
        v: View,
        @MenuRes menuRes: Int,
        adapter: PublicTimeSlotRecyclerViewAdapter
    ) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.menu.forEach {
            if (it.title == filter) {
                it.isChecked = true
            }
        }

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            menuItem.isChecked = true

            if (menuItem.title == "No filter") {
                filter = "No filter"
                adapter.setFilter {
                    true
                }
            }
            if (menuItem.title == "Title") {
                filter = "Title"
                val dialog =
                    this.layoutInflater.inflate(R.layout.dialog_filter_criteria_string, null)
                val builder = AlertDialog.Builder(this.context).setView(dialog)

                val alertDialog = builder.show()
                val button = dialog.findViewById<Button>(R.id.button)
                val editText = dialog.findViewById<EditText>(R.id.editTextFilterDialog)
                button.setOnClickListener {
                    //Add a filter
                    adapter.setFilter {
                        it.title.contains(editText.text.toString(), ignoreCase = true)
                    }
                    alertDialog.dismiss()
                }
            }
            if (menuItem.title == "Location") {
                filter = "Location"
                val dialog =
                    this.layoutInflater.inflate(R.layout.dialog_filter_criteria_string, null)
                val builder = AlertDialog.Builder(this.context).setView(dialog)

                val alertDialog = builder.show()
                val button = dialog.findViewById<Button>(R.id.button)
                val editText = dialog.findViewById<EditText>(R.id.editTextFilterDialog)
                button.setOnClickListener {
                    //Add a filter
                    adapter.setFilter {
                        it.location.contains(editText.text.toString(), ignoreCase = true)
                    }
                    alertDialog.dismiss()
                }
            }
            if (menuItem.title == "Description") {
                filter = "Description"
                val dialog =
                    this.layoutInflater.inflate(R.layout.dialog_filter_criteria_string, null)
                val builder = AlertDialog.Builder(this.context).setView(dialog)

                val alertDialog = builder.show()
                val button = dialog.findViewById<Button>(R.id.button)
                val editText = dialog.findViewById<EditText>(R.id.editTextFilterDialog)
                button.setOnClickListener {
                    //Add a filter
                    adapter.setFilter {
                        it.description.contains(editText.text.toString(), ignoreCase = true)
                    }
                    alertDialog.dismiss()
                }
            }

            //TODO: Duration
//            if (menuItem.title == "Duration") {
//                val dialog =
//                    this.layoutInflater.inflate(R.layout.dialog_filter_criteria_string, null)
//                val builder = AlertDialog.Builder(this.context).setView(dialog)
//
//                val alertDialog = builder.show()
//                val button = dialog.findViewById<Button>(R.id.button)
//                val editText = dialog.findViewById<EditText>(R.id.editTextFilterDialog)
//                button.setOnClickListener {
//                    //Add a filter
//                    adapter.setFilter {
//                        it.duration.contains(editText.text.toString(), ignoreCase = true)
//                    }
//                    alertDialog.dismiss()
//                }
//            }
            if (menuItem.title == "Date and Time") {
                filter = "Date and Time"
                val dialog = this.layoutInflater.inflate(R.layout.dialog_duration, null)
                val builder = AlertDialog.Builder(this.context).setView(dialog)

                val alertDialog = builder.show()
                val button = dialog.findViewById<Button>(R.id.button)
                button.setOnClickListener {
                    alertDialog.dismiss()
                }
            }

            true
        }

        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {

            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
}

//TODO: test
enum class AdvsFilter {
    ALL, TITLE
}