package com.pi.projectinclusion

// TODO: If you are using androidx
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.constraintlayout.widget.ConstraintLayout


//private const val TAG = "AnswerRadioButton"

class AnswerRadioButton : AppCompatRadioButton {
    private var view: View? = null
    private var tv: TextView? = null
    private var cl: ConstraintLayout? = null
    private var iv: ImageView? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    @SuppressLint("InflateParams")
    private fun init(context: Context) {
        view = LayoutInflater.from(context).inflate(R.layout.layout_radio_button, null)
        tv = view!!.findViewById(R.id.tvRB)
        iv = view!!.findViewById(R.id.ivRB)
        cl = view!!.findViewById(R.id.clRB)
        redrawLayout()
        //redrawLayout()
    }

    private fun redrawLayout() {
        // view!!.isDrawingCacheEnabled = true

        val width = Resources.getSystem().displayMetrics.widthPixels - dp2px(context, 100)
        view!!.layout(0, 0, width, dp2px(context,80))
        //  view!!.buildDrawingCache(true)
        val bitmap = getBitmapFromView(view!!)
        setCompoundDrawablesWithIntrinsicBounds(BitmapDrawable(resources, bitmap), null, null, null)
        //view!!.isDrawingCacheEnabled = false
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width, view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun dp2px(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}