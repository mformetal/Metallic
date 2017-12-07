package mformetal.metallic.util

import android.content.Context
import android.support.test.InstrumentationRegistry
import mformetal.metallic.MockApp
import mformetal.metallic.dagger.TestAppComponent

/**
 * Created by peelemil on 12/7/17.
 */
val mockApp : MockApp
    get() = InstrumentationRegistry.getTargetContext().applicationContext as MockApp

val testAppComponent : TestAppComponent
    get() = mockApp.component as TestAppComponent

val targetContext: Context
    get() = InstrumentationRegistry.getTargetContext()