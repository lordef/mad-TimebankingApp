package it.polito.mad.lab02.fragments.listofskills

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import it.polito.mad.lab02.R
import it.polito.mad.lab02.viewmodels.PublicTimeSlotListViewModel


class PublicTimeSlotDetailsFragment : Fragment(R.layout.fragment_public_time_slot_details) {

    private val vm by activityViewModels<PublicTimeSlotListViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val title = view.findViewById<TextView>(R.id.titleTextView)
        val description = view.findViewById<TextView>(R.id.descriptionTextView)
        val dateTime = view.findViewById<TextView>(R.id.dateTimeTextView)
        val duration = view.findViewById<TextView>(R.id.durationTextView)
        val location = view.findViewById<TextView>(R.id.locationTextView)

        val profile = view.findViewById<TextView>(R.id.profileTextView)

        val id = arguments?.getString("id")
        if (id != null) {
            vm.timeslotList
//                .getTimeSlot(id)
                .observe(viewLifecycleOwner) {
                    val ts = it.filter { t -> t.id == id }[0]
                    Log.d("myTag", it.toString())

                    title.text = ts.title
                    description.text = ts.description
                    dateTime.text = ts.dateTime
                    val d = ts.duration.split(":")

                    if (d.size == 2) {
                        duration.text = "" + d[0] + "h " + d[1] + "min"
                    } else {
                        duration.text = ""
                    }
                    location.text = ts.location

                    profile.text = ts.userProfile.nickname
                    profile.setOnClickListener{
                        val bundle = Bundle()
                        bundle.putString("id", ts.id)
                        findNavController()
                            .navigate(
                                R.id.action_publicTimeSlotDetailsFragment_to_publicShowProfileFragment,
                                bundle
                            )
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