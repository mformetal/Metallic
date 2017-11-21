package mformetal.metallic.util

import android.arch.lifecycle.Observer
import android.view.LayoutInflater
import android.view.View

/**
 * Created by mbpeele on 11/19/17.
 */
val View.inflater : LayoutInflater
    get() = LayoutInflater.from(context)

fun <T> nonNullObserver(function: (T) -> Unit) : Observer<T> {
    return Observer { t ->
        if (t == null) {
            return@Observer
        }

        function.invoke(t)
    }
}