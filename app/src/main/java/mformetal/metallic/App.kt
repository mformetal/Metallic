package mformetal.metallic

import android.app.Application
import com.evernote.android.job.JobManager
import io.realm.Realm
import io.realm.RealmConfiguration
import mformetal.metallic.core.MetallicJobCreator
import mformetal.metallic.dagger.AppComponent
import mformetal.metallic.dagger.AppModule
import mformetal.metallic.dagger.DaggerAppComponent
import javax.inject.Inject

/**
 * Created by mbpeele on 11/17/17.
 */
class App : Application() {

    lateinit var component : AppComponent

    @Inject
    lateinit var jobCreator : MetallicJobCreator

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        Realm.setDefaultConfiguration(
                RealmConfiguration.Builder()
                        .deleteRealmIfMigrationNeeded()
                        .build()
        )

        component = DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .build()
        component.injectMembers(this)

        JobManager.create(this).addJobCreator(jobCreator)
    }
}

