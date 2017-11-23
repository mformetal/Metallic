package mformetal.metallic.util

import android.arch.lifecycle.Observer

/**
 * @author - mbpeele on 11/23/17.
 */
private class NonNullObserver<T>(val block: (T) -> (Unit)) : Observer<T> {

    override fun onChanged(t: T?) {
        t?.let(block)
    }
}

fun <T> observer(block: (T) -> Unit) : Observer<T> = NonNullObserver(block)