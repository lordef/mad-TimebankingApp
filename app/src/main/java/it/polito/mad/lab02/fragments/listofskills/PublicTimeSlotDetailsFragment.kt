package it.polito.mad.lab02.fragments.listofskills

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.view.menu.MenuView
import androidx.cardview.widget.CardView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import it.polito.mad.lab02.R
import it.polito.mad.lab02.models.TimeSlot
import it.polito.mad.lab02.viewmodels.MainActivityViewModel


class PublicTimeSlotDetailsFragment : Fragment(R.layout.fragment_public_time_slot_details) {

    private val vm by activityViewModels<MainActivityViewModel>()
    private val _optionsMenu = MutableLiveData<Menu?>()
    private val optionsMenu: LiveData<Menu?> = _optionsMenu

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
        val timeslot = arguments?.getString("timeslot")

        //coming from the chat
        if (timeslot != null) {

            val ts = Gson().fromJson(timeslot, TimeSlot::class.java)

            optionsMenu.observe(viewLifecycleOwner) { menu ->
                if (menu != null) {
                    if (menu.findItem(R.id.messageItem) != null) {
                        menu.findItem(R.id.messageItem).isVisible =
                            ts.userProfile.uid != FirebaseAuth.getInstance().currentUser?.uid
                    }
                }
            }
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

            profileCard.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("id", ts.id)
                bundle.putString("origin", "interests")
                findNavController()
                    .navigate(
                        R.id.action_publicTimeSlotDetailsFragment_to_publicShowProfileFragment,
                        bundle
                    )
            }

        } else if (id != null) {
            vm.timeslotList
                .observe(viewLifecycleOwner) {
                    val ts = it.filter { t -> t.id == id }[0]

                    optionsMenu.observe(viewLifecycleOwner) { menu ->
                        if (menu != null) {
                            if (menu.findItem(R.id.messageItem) != null) {
                                menu.findItem(R.id.messageItem).isVisible =
                                    ts.userProfile.uid != FirebaseAuth.getInstance().currentUser?.uid
                            }
                        }
                    }

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

                    profileCard.setOnClickListener {
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
        _optionsMenu.value = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.messageItem -> {
                Toast.makeText(this.context, "Contact publisher", Toast.LENGTH_SHORT)
                    .show()
                view?.let { view ->
                    val timeslot = arguments?.getString("timeslot")
                    val id = arguments?.getString("id")
                    //coming from the chat
                    if (timeslot != null) {
                        val ts = Gson().fromJson(timeslot, TimeSlot::class.java)
                        val bundle = Bundle()
                        val id = vm.getChat(ts)
                        if (id != null) {
                            bundle.putString("id", id)
                        }
                        bundle.putString("timeslot", Gson().toJson(ts))
                        findNavController()
                            .navigate(
                                R.id.action_publicTimeSlotDetailsFragment_to_nav_single_message,
                                bundle
                            )
                    } else {
                        vm.timeslotList
                            .observe(viewLifecycleOwner) { listTs ->
                                val bundle = Bundle()
                                val ts = listTs.first { it.id == id }
                                val id = vm.getChat(ts)
                                if (id != null) {
                                    bundle.putString("id", id)
                                }
                                bundle.putString("timeslot", Gson().toJson(ts))
                                Navigation.findNavController(view).navigate(
                                    R.id.action_publicTimeSlotDetailsFragment_to_nav_single_message,
                                    bundle
                                )
                            }
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