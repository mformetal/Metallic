package mformetal.metallic

import android.app.Application
import com.evernote.android.job.JobManager
import io.realm.Realm
import io.realm.RealmConfiguration
import mformetal.metallic.dagger.*
import javax.inject.Inject

/**
 * Created by mbpeele on 11/17/17.
 */
class App : Application() {

    lateinit var component : AppComponent

    // For Dagger...
    @Suppress("unused")
    @Inject
    lateinit var jobManager : JobManager

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

        WatchListJob.schedule()
        SyncJob.schedule()
    }
}

