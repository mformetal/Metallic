package mformetal.metallic.core

/**
 * @author - mbpeele on 11/23/17.
 */
interface PreferencesRepository {

    fun hasUserOnboarded() : Boolean

    fun setHasOnboarded()
}