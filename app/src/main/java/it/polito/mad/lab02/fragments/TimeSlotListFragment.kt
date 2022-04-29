package it.polito.mad.lab02.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import it.polito.mad.lab02.viewmodels.TimeSlotListViewModel
import it.polito.mad.lab02.databinding.FragmentTimeSlotListBinding

class TimeSlotListFragment : Fragment() {

    private var _binding: FragmentTimeSlotListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val advertisementsViewModel =
            ViewModelProvider(this).get(TimeSlotListViewModel::class.java)

        _binding = FragmentTimeSlotListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textAdvertisements
        advertisementsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}