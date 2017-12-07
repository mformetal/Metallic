package mformetal.metallic.core

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author - mbpeele on 11/23/17.
 */
@Singleton
class SharedPreferencesRepository @Inject constructor(private val sharedPreferences: SharedPreferences) : PreferencesRepository {

    val HAS_ONBOARDED_KEY = "hasOnboarded"

    override fun hasUserOnboarded() : Boolean = sharedPreferences.getBoolean(HAS_ONBOARDED_KEY, false)

    override fun setHasOnboarded(hasOnboarded: Boolean) {
        sharedPreferences.edit()
                .putBoolean(HAS_ONBOARDED_KEY, hasOnboarded)
                .apply()
    }
}