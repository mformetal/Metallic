package mformetal.metallic

import android.app.Application
import mformetal.metallic.dagger.AppComponent
import mformetal.metallic.dagger.AppModule
import mformetal.metallic.dagger.DaggerAppComponent

/**
 * Created by mbpeele on 11/17/17.
 */
class App : Application() {

    lateinit var component : AppComponent

    override fun onCreate() {
        super.onCreate()

        component = DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .build()
        component.injectMembers(this)
    }
}

