package mformetal.metallic.core

import android.content.Context
import mformetal.metallic.home.HomeActivity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author - mbpeele on 11/23/17.
 */
@Singleton
class Router @Inject constructor(val context: Context) {

    fun onboard() {
        val intent = HomeActivity.create(context)
        context.startActivity(intent)
    }
}