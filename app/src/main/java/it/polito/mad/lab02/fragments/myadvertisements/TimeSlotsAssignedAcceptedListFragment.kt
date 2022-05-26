package it.polito.mad.lab02.fragments.myadvertisements

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.MenuRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.polito.mad.lab02.R
import it.polito.mad.lab02.fragments.listofskills.PublicTimeSlotRecyclerViewAdapter
import it.polito.mad.lab02.viewmodels.MainActivityViewModel
import kotlin.system.exitProcess

/**
 * A fragment representing a list of Items.
 */
class TimeSlotsAssignedAcceptedListFragment : Fragment(R.layout.fragment_time_slots_assigned_accepted_list) {

    private var columnCount = 1

    private val vm by activityViewModels<MainActivityViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}