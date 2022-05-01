package it.polito.mad.lab02.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import it.polito.mad.lab02.viewmodels.ProfileViewModel
import it.polito.mad.lab02.databinding.FragmentProfileBinding
import kotlin.system.exitProcess

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textProfile
        profileViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        //TODO: let it be
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if(!findNavController().navigateUp()){
                    exitProcess(1)
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}