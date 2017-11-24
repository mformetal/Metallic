package mformetal.metallic.core

import android.content.Context
import android.content.Intent
import mformetal.metallic.home.HomeActivity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author - mbpeele on 11/23/17.
 */
@Singleton
class Router @Inject constructor(val applicationContext: Context) {

    fun onboard() {
        val intent = HomeActivity.create(applicationContext)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
    }
}