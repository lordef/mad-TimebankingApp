package it.polito.mad.lab02.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import it.polito.mad.lab02.R
import it.polito.mad.lab02.SharedPreference
import it.polito.mad.lab02.models.TimeSlot
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TimeSlotEditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimeSlotEditFragment : Fragment(R.layout.fragment_time_slot_edit) {
    /*
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_slot_edit, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TimeSlotEditFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TimeSlotEditFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
*/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val button = view.findViewById<Button>(R.id.button)
        button.setOnClickListener(View.OnClickListener { addSP() })

        val displayDateTextView = view.findViewById<TextView>(R.id.dateEdit)
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        displayDateTextView.setOnClickListener(View.OnClickListener {


            val dialog = DatePickerDialog(this.requireContext(), DatePickerDialog.OnDateSetListener { view, year, month, day ->
                displayDateTextView.text = "" + day + "/" + month + "/" + year
            }, year, month, day)

            dialog.show()

        })

        val displayTimeTextView = view.findViewById<TextView>(R.id.timeEdit)
        displayTimeTextView.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                displayTimeTextView.text = SimpleDateFormat("HH:mm").format(calendar.time)
            }
            TimePickerDialog(this.context, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }

    }

    fun addSP() {
        val title = view?.findViewById<EditText>(R.id.titleEditText)
        val description = view?.findViewById<EditText>(R.id.descriptionEditText)
        val date = view?.findViewById<TextView>(R.id.dateEdit)
        val time = view?.findViewById<TextView>(R.id.timeEdit)
        val dateTime = "" + date.toString() + " " + time.toString()
        val duration = view?.findViewById<EditText>(R.id.durationEditText)
        val location = view?.findViewById<EditText>(R.id.locationEditText)
        val obj = TimeSlot(
            title?.text.toString(),
            description?.text.toString(),
            dateTime,
            duration?.text.toString(),
            location?.text.toString()
        )
        val sp = this.context?.let { SharedPreference(it) }
        if (sp != null) {
            if (title != null) {
                sp.setTimeSlot(title.text.toString(), obj)
            }
        }
    }

}