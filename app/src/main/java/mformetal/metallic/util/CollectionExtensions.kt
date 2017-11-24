package mformetal.metallic.util

/**
 * @author - mbpeele on 11/23/17.
 */
fun <T> Collection<T>.doesNotContain(element: T) : Boolean = !contains(element)