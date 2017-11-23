package mformetal.metallic.util

import android.arch.lifecycle.Observer
import android.view.LayoutInflater
import android.view.View

/**
 * @author - mbpeele on 11/19/17.
 */
val View.inflater : LayoutInflater
    get() = LayoutInflater.from(context)

fun View.visible() {
    visibility = View.VISIBLE
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