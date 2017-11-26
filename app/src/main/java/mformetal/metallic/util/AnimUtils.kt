package mformetal.metallic.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.util.Property
import android.view.View



/**
 * @author - mbpeele on 11/25/17.
 */
object AnimUtils {

    abstract class FloatProperty<T>(name: String) : Property<T, Float>(Float::class.java, name) {

        abstract fun setValue(`object`: T, value: Float)

        override operator fun set(`object`: T, value: Float?) {
            setValue(`object`, value!!)
        }
    }

    fun visible(view: View) : ObjectAnimator {
        val alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f)
        alpha.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                view.visibility = View.VISIBLE
            }
        })
        return alpha
    }
}