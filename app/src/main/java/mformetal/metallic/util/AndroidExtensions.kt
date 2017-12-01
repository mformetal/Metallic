package mformetal.metallic.util

import android.content.Context
import android.graphics.Point
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager

/**
 * @author - mbpeele on 11/19/17.
 */
val View.inflater : LayoutInflater
    get() = LayoutInflater.from(context)