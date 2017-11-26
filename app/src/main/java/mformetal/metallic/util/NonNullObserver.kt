package mformetal.metallic.util

import android.arch.lifecycle.Observer

/**
 * @author - mbpeele on 11/26/17.
 */
private class NonNullObserver<T>(private val block: (T) -> Unit) : Observer<T> {

    override fun onChanged(t: T?) {
        t?.let(block)
    }
}

fun <T> safeObserver(block: (T) -> Unit) : Observer<T> = NonNullObserver(block)