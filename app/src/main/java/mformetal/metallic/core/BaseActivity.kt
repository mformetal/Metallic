package mformetal.metallic.core

import androidx.appcompat.app.AppCompatActivity
import mformetal.metallic.App

/**
 * Created by mbpeele on 11/17/17.
 */
open class BaseActivity : AppCompatActivity() {

    val app : App
        get() = application as App

}