package it.polito.mad.lab02

import android.content.ContentResolver
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.OrientationEventListener
import androidx.databinding.BindingAdapter
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

object Utils {
    @JvmStatic
    fun setUriInImageView(imageView: ImageView, imageUri: Uri, contentResolver: ContentResolver) {
        BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri)).also { bitmap ->
            imageView.setImageBitmap(bitmap)
        }
    }

    @JvmStatic
    fun divideDisplayInPortion(
        firstLayout: ConstraintLayout,
        secondLayer: ViewGroup,
        orientation: Int
    ) {
        secondLayer.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val h = secondLayer.height
                val w = secondLayer.width
                firstLayout.post {
                    firstLayout.layoutParams =
                        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
                            LinearLayout.LayoutParams(w / 3, h)
                        else
                            LinearLayout.LayoutParams(w, h / 3)
                }
                secondLayer.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }


    @JvmStatic
    fun minutesInHoursAndMinutesString(minutes: Int): String {
        val hrs = minutes / 60
        val min = minutes % 60

        return if (hrs == 0) {
            "${min}m"
        } else {
            "${hrs}h ${min}m"

        }
    }

    @JvmStatic
    fun fromHHMMToString(time: String): String {
        return "" + time.split(":")[0] + "h " + time.split(":")[1] + "min"
    }

    @JvmStatic
    fun fromStringToHHMM(time: String): String {
        return "" + time.split(" ")[0].split("h")[0] + ":" + time.split(" ")[1].split("min")[0]
    }

}