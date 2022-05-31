package it.polito.mad.lab02.fragments.listofskills

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
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
import it.polito.mad.lab02.viewmodels.MainActivityViewModel
import java.util.*

/**
 * A fragment representing a list of Items.
 */
class PublicTimeSlotFragment : Fragment(R.layout.fragment_public_time_slot_list_filter_sort) {

    private var columnCount = 1

    private val vm by activityViewModels<MainActivityViewModel>()

    private var fragmentLabel = ""

    var filter = "No filter"
    var sort = "No sorting"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onViewCreated(view, savedInstanceState)

//        val recyclerView = view.findViewById<RecyclerView>(R.id.public_time_slot_list)
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)

        val skillRefToString = arguments?.getString("skill")

        if (skillRefToString != null) {
            vm.setPublicAdvsListenerBySkill(skillRefToString)

            //Setting App bar heading
            fragmentLabel = skillRefToString.split("/").last()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            (activity as AppCompatActivity?)?.supportActionBar?.title = fragmentLabel

            //Setting title of the page
            var pageTitle = view.findViewById<TextView>(R.id.timeslotTileTextView)
            pageTitle.text = skillRefToString.split("/").last()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() } +" "+pageTitle.text

            vm.timeslotList.observe(viewLifecycleOwner) { timeSlotList ->

                val allFilterButton = view.findViewById<Button>(R.id.sortButton)
                allFilterButton.setOnClickListener {
                    vm.addFilter {
                        true
                    }
                }

                val textView = view.findViewById<TextView>(R.id.text_pub_advertisements)
                if (timeSlotList.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    textView.visibility = View.VISIBLE
                } else {
                    textView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }


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
                    showFilterMenu(filterButton, R.menu.filter_criteria_menu, myAdapter)

                }

                val sortButton = view.findViewById<Button>(R.id.sortButton)
                sortButton.setOnClickListener {
                    showSortMenu(sortButton, R.menu.sort_criteria_menu, myAdapter)

                }



            }

            val callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    view.findNavController().navigateUp()
                    onBackPressed()
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(callback)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        (activity as AppCompatActivity?)?.supportActionBar?.title = fragmentLabel
    }

    private fun showSortMenu(
        v: View,
        @MenuRes menuRes: Int,
        adapter: PublicTimeSlotRecyclerViewAdapter
    ) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.menu.forEach {
            if (sort.contains(it.title)) {
                it.isChecked = true
                if (it.title != "No sorting" && sort.contains("↓")) {
                    it.title = "↓ " + it.title.toString()
                }
                if (it.title != "No sorting" && sort.contains("↑")) {
                    it.title = "↑ " + it.title.toString()
                }
            }
        }

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            menuItem.isChecked = true

            if (menuItem.title == "No sorting") {
                adapter.setOrder("No sorting")
                sort = "No sorting"

            }
            if (menuItem.title.contains("Title")) {
                if (sort.contains("Title")) {
                    if (sort == "↓ Title") {
                        menuItem.title = "↑ Title"
                        adapter.setOrder("Title")
                    } else {
                        menuItem.title = "↓ Title"
                        adapter.setOrder("Title_desc")
                    }
                } else {
                    menuItem.title = "↑ " + menuItem.title.toString()
                    adapter.setOrder("Title")
                }
                sort = menuItem.title.toString()
            }
            if (menuItem.title.contains("Location")) {
                if (sort.contains("Location")) {
                    if (sort == "↓ Location") {
                        menuItem.title = "↑ Location"
                        adapter.setOrder("Location")
                    } else {
                        menuItem.title = "↓ Location"
                        adapter.setOrder("Location_desc")
                    }
                } else {
                    menuItem.title = "↑ " + menuItem.title.toString()
                    adapter.setOrder("Location")
                }
                sort = menuItem.title.toString()
            }
            if (menuItem.title.contains("Duration")) {
                if (sort.contains("Duration")) {
                    if (sort == "↓ Duration") {
                        menuItem.title = "↑ Duration"
                        adapter.setOrder("Duration")
                    } else {
                        menuItem.title = "↓ Duration"
                        adapter.setOrder("Duration_desc")
                    }
                } else {
                    menuItem.title = "↑" + menuItem.title.toString()
                    adapter.setOrder("Duration")
                }
                sort = menuItem.title.toString()
            }
            if (menuItem.title.contains("Date and Time")) {
                if (sort.contains("Date and Time")) {
                    if (sort == "↓ Date and Time") {
                        menuItem.title = "↑ Date and Time"
                        adapter.setOrder("Date and Time")
                    } else {
                        menuItem.title = "↓ Date and Time"
                        adapter.setOrder("Date and Time_desc")
                    }
                } else {
                    menuItem.title = "↑" + menuItem.title.toString()
                    adapter.setOrder("Date and Time")
                }
                sort = menuItem.title.toString()
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

            if (menuItem.title == "Date") {
                filter = "Date"
                var dateSelected = String()
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val dialog =
                    this.layoutInflater.inflate(R.layout.dialog_date_time, null)
                val builder = AlertDialog.Builder(this.context).setView(dialog)
                val alertDialog = builder.show()
                val dateText = dialog.findViewById<TextView>(R.id.pickDate)

                dateText!!.setOnClickListener(View.OnClickListener {

                    val dialog1 = DatePickerDialog(
                        this.requireContext(),
                        DatePickerDialog.OnDateSetListener { _, year, month, day ->
                            val month1 = 1 + month
                            dateText.text = "" + day + "/" + month1 + "/" + year
                        },
                        year,
                        month,
                        day
                    )

                    dialog1.show()
                })


                val button = dialog.findViewById<Button>(R.id.button2)
                button.setOnClickListener {
                    if (dateText.text.toString() != "Select date") {
                        dateSelected = dateText.text.toString()
                    } else if (dateText.text.toString() == "Select date") {
                        dateSelected = "1/1/2022"
                    }
                    //Add a filter
                    adapter.setFilter {
                        it.dateTime.contains(dateSelected, ignoreCase = true)
                    }
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
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun onBackPressed(){
        val runnable = Runnable {
            // useful to call interaction with viewModel
            vm.removePublicAdvsListener()
        }
        // Perform persistence changes after 250 millis
        Handler().postDelayed(runnable, 250)
    }

}