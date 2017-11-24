package mformetal.metallic.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.arch.lifecycle.Observer
import android.view.LayoutInflater
import android.view.View

private const val DEFAULT_VISBILITY_DURATION = 350L

/**
 * @author - mbpeele on 11/19/17.
 */
val View.inflater : LayoutInflater
    get() = LayoutInflater.from(context)

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.visible(duration: Int) : ObjectAnimator {
    val animator = ObjectAnimator.ofFloat(this, View.ALPHA, 0f, 1f)
    animator.duration = DEFAULT_VISBILITY_DURATION
    animator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator) {
            visibility = View.VISIBLE
        }
    })
    return animator
}

fun View.gone(duration: Int) : ObjectAnimator {
    val gone = ObjectAnimator.ofFloat(this, View.ALPHA, 1f, 0f)
    gone.duration = DEFAULT_VISBILITY_DURATION
    gone.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            visibility = View.GONE
        }
    })
    return gone
}

fun View.gone() {
    visibility = View.GONE
}

fun <T> nonNullObserver(function: (T) -> Unit) : Observer<T> {
    return Observer { t ->
        if (t == null) {
            return@Observer
        }

        function.invoke(t)
    }
}