package it.polito.mad.lab02.fragments.listofskills

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.annotation.Px
import androidx.core.view.forEach
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.DynamicAnimation.ViewProperty
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.*
import it.polito.mad.lab02.R
import it.polito.mad.lab02.databinding.FragmentAllSkillsBinding
import it.polito.mad.lab02.databinding.FragmentSkillListListBinding
import it.polito.mad.lab02.util.spring
import it.polito.mad.lab02.viewmodels.MainActivityViewModel
import kotlin.system.exitProcess

class SkillListFragment : Fragment(R.layout.fragment_all_skills) {

    private var columnCount = 1

    private val vm by activityViewModels<MainActivityViewModel>()

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val binding = FragmentAllSkillsBinding.inflate(inflater, container, false)
////            .apply {
////            val recyclerView = allSkillsList
////            val pageTitle = skillListTitleTextView
////
////            vm.skillList.observe(viewLifecycleOwner) { skillList ->
////                if (recyclerView is RecyclerView) {
////                    with(recyclerView) {
////                        layoutManager = when {
////                            columnCount <= 1 -> {
//////                            LinearLayoutManager(fragContext)
////                                StaggeredGridLayoutManager(8, StaggeredGridLayoutManager.HORIZONTAL)
////                            }
////                            else -> GridLayoutManager(context, columnCount)
////                        }
////                        adapter = SkillListRecyclerViewAdapter(skillList)
////                        smoothScrollToPositionWithSpeed(skillList.size)
////                        addOnScrollListener(
////                            OscillatingScrollListener(resources.getDimensionPixelSize(R.dimen.grid_2))
////                        )
////                    }
////                }
////
////                val textView = textSkills
////                if (skillList.isEmpty()) {
////                    recyclerView.visibility = View.GONE
////                    pageTitle.visibility = View.GONE
////                    textView.visibility = View.VISIBLE
////                } else {
////                    textView.visibility = View.GONE
////                    recyclerView.visibility = View.VISIBLE
////                }
////            }
//
////        }
//        return binding.root
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.allSkillsList)

        val pageTitle = view.findViewById<TextView>(R.id.skillListTitleTextView)

        vm.skillList.observe(viewLifecycleOwner) { skillList ->
            if (recyclerView is RecyclerView) {
                with(recyclerView) {
//                    layoutManager = when {
//                        columnCount <= 1 -> {
////                            LinearLayoutManager(fragContext)
//                            StaggeredGridLayoutManager(8, StaggeredGridLayoutManager.HORIZONTAL)
//                        }
//                        else -> GridLayoutManager(context, columnCount)
//                    }
                    adapter = SkillListRecyclerViewAdapter(skillList)
                    smoothScrollToPositionWithSpeed(skillList.size)
                    addOnScrollListener(
                        OscillatingScrollListener(resources.getDimensionPixelSize(R.dimen.grid_2))
                    )
                }
            }

            val textView = view.findViewById<TextView>(R.id.text_skills)
            if (skillList.isEmpty()) {
                recyclerView.visibility = View.GONE
                pageTitle.visibility = View.GONE
                textView.visibility = View.VISIBLE
            } else {
                textView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }

        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if(!view.findNavController().navigateUp()){
                    exitProcess(1)
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

    }
}

/**
 * Oscillates a [RecyclerView]'s children based on the horizontal scroll velocity.
 */
private const val MAX_OSCILLATION_ANGLE = 6f // ±6º

class OscillatingScrollListener(
    @Px private val scrollDistance: Int
) : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        // Calculate a rotation to set from the horizontal scroll
        val clampedDx = dx.coerceIn(-scrollDistance, scrollDistance)
        val rotation = (clampedDx / scrollDistance) * MAX_OSCILLATION_ANGLE
        recyclerView.forEach {
            // Alter the pivot point based on scroll direction to make motion look more natural
            it.pivotX = it.width / 2f + clampedDx / 3f
            it.pivotY = it.height / 3f
            it.spring(SpringAnimation.ROTATION).animateToFinalPosition(rotation)
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState != RecyclerView.SCROLL_STATE_DRAGGING) {
            recyclerView.forEach {
                it.spring(SpringAnimation.ROTATION).animateToFinalPosition(0f)
            }
        }
    }
}

fun RecyclerView.smoothScrollToPositionWithSpeed(
    position: Int,
    millisPerInch: Float = 100f
) {
    val lm = requireNotNull(layoutManager)
    val scroller = object : LinearSmoothScroller(context) {
        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
            return millisPerInch / displayMetrics.densityDpi
        }
    }
    scroller.targetPosition = position
    lm.startSmoothScroll(scroller)
}