package mformetal.metallic.util

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

/**
 * @author - Miles Peele on 11/29/17.
 */
class FourThreeImageView : ImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val fourThreeHeight = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthSpec) * 3 / 4,
                View.MeasureSpec.EXACTLY)
        super.onMeasure(widthSpec, fourThreeHeight)
    }
}