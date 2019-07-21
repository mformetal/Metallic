package mformetal.metallic.util

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import android.util.AttributeSet
import mformetal.metallic.R

/**
 * @author - mbpeele on 11/24/17.
 */
open class AspectRatioImageView : AppCompatImageView {

    private var aspectRatio: Float = DEFAULT_ASPECT_RATIO
        set(value) {
            field = value
            requestLayout()
        }

    private var dominantMeasurement: Int = 0
        set(value) {
            if (dominantMeasurement != MEASUREMENT_HEIGHT && dominantMeasurement != MEASUREMENT_WIDTH) {
                throw IllegalArgumentException("Invalid measurement type.")
            }
            field = value
            requestLayout()
        }

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attributeSet: AttributeSet?) {
        if (attributeSet != null) {
            val a = context.obtainStyledAttributes(attributeSet, R.styleable.AspectRatioImageView)
            aspectRatio = a.getFloat(R.styleable.AspectRatioImageView_aspectRatio, DEFAULT_ASPECT_RATIO)
            dominantMeasurement = a.getInt(R.styleable.AspectRatioImageView_dominantMeasurement,
                    DEFAULT_DOMINANT_MEASUREMENT)
            a.recycle()
        }
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val newWidth: Int
        val newHeight: Int
        when (dominantMeasurement) {
            MEASUREMENT_WIDTH -> {
                newWidth = widthSpec
                newHeight = MeasureSpec.makeMeasureSpec(
                        (MeasureSpec.getSize(widthSpec) * aspectRatio).toInt(),
                        MeasureSpec.EXACTLY)
            }

            MEASUREMENT_HEIGHT -> {
                newHeight = heightSpec
                newWidth = MeasureSpec.makeMeasureSpec(
                        (MeasureSpec.getSize(heightSpec) * aspectRatio).toInt(),
                        MeasureSpec.EXACTLY)
            }

            else -> throw IllegalStateException("Unknown measurement with ID " + dominantMeasurement)
        }

        super.onMeasure(newWidth, newHeight)
    }

    companion object {

        val MEASUREMENT_WIDTH = 0
        val MEASUREMENT_HEIGHT = 1

        private val DEFAULT_ASPECT_RATIO = 1f
        private val DEFAULT_DOMINANT_MEASUREMENT = MEASUREMENT_WIDTH
    }
}