package it.polito.mad.lab02

import android.content.ContentResolver
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.OrientationEventListener
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout

object Utils {
    @JvmStatic
    fun setUriInImageView(imageView: ImageView, imageUri: Uri, contentResolver:ContentResolver) {
        BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri)).also { bitmap ->
            imageView.setImageBitmap(bitmap)
        }
    }

    @JvmStatic
    fun divideDisplayInPortion(firstLayout : ConstraintLayout, secondLayer : ViewGroup, orientation: Int){
        secondLayer.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val h = secondLayer.height
                val w = secondLayer.width
                Log.d("Layout", "firstLayout.requestLayout(): $w,$h")
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
}