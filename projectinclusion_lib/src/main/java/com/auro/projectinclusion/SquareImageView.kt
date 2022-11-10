package com.pi.projectinclusion

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView


class SquareCardView : CardView {
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!,
        attrs,
        defStyle
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var height: Int = measuredHeight

        setMeasuredDimension(height, height)
        radius = height/2f;
    }
}