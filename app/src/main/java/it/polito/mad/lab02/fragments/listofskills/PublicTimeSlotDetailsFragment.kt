package it.polito.mad.lab02.fragments.listofskills

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import coil.load
import it.polito.mad.lab02.R
import it.polito.mad.lab02.viewmodels.MainActivityViewModel


class PublicTimeSlotDetailsFragment : Fragment(R.layout.fragment_public_time_slot_details) {

    private val vm by activityViewModels<MainActivityViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val title = view.findViewById<TextView>(R.id.titleTextView)
        val description = view.findViewById<TextView>(R.id.descriptionTextView)
        val dateTime = view.findViewById<TextView>(R.id.dateTimeTextView)
        val duration = view.findViewById<TextView>(R.id.durationTextView)
        val location = view.findViewById<TextView>(R.id.locationTextView)
        val skill = view.findViewById<TextView>(R.id.skillTextView)
        val profileCard = view.findViewById<CardView>(R.id.profileCardView)
        val profileImage = view.findViewById<ImageView>(R.id.imageView2)

        val profile = view.findViewById<TextView>(R.id.profileTextView)

        val id = arguments?.getString("id")
        if (id != null) {
            vm.timeslotList
                .observe(viewLifecycleOwner) {
                    val ts = it.filter { t -> t.id == id }[0]

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
                    profileImage.load(ts.userProfile.imageUri)
                    skill.text = ts.skill.split("/").last()

                    profileCard.setOnClickListener{
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.message_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.messageItem -> {
                Toast.makeText(this.context, "Contact publisher", Toast.LENGTH_SHORT)
                    .show()
                view?.let { view ->
                    val id = arguments?.getString("id")
                    vm.timeslotList
                        .observe(viewLifecycleOwner) { listTs ->
                            val bundle = Bundle()
                            val ref = vm.createChat(listTs.first { it.id == id })
                            bundle.putString("ref", ref)
                            Navigation.findNavController(view).navigate(
                                R.id.action_publicTimeSlotDetailsFragment_to_nav_single_message, bundle
                            )
                        }
                }
                true
            }
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }


}