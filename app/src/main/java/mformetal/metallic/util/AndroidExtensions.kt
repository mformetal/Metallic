package mformetal.metallic.util

import android.view.LayoutInflater
import android.view.View

/**
 * Created by mbpeele on 11/19/17.
 */
val View.inflater : LayoutInflater
    get() = LayoutInflater.from(context)