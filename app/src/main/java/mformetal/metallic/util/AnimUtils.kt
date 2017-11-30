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

    fun visible(view: View) : ObjectAnimator {
        val alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f)
        alpha.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                view.visibility = View.VISIBLE
            }
        })
        return alpha
    }

    fun gone(view: View) : ObjectAnimator {
        val alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f)
        alpha.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                view.visibility = View.VISIBLE
            }
        })
        return alpha
    }
}