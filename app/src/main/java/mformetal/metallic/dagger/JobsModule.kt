package mformetal.metallic.dagger

import com.evernote.android.job.Job
import com.evernote.android.job.JobManager
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import mformetal.metallic.App
import mformetal.metallic.BuildConfig
import mformetal.metallic.core.MetallicJobCreator
import mformetal.metallic.domain.api.spotify.SpotifyAPI
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Created by peelemil on 12/1/17.
 */
@Module
class JobsModule {

    @Provides
    @Singleton
    fun jobManager(app: App, jobCreator: MetallicJobCreator) : JobManager {
        JobManager.create(app).addJobCreator(jobCreator)
        return JobManager.instance()
    }

    @Provides
    fun creator(map: Map<String, @JvmSuppressWildcards Provider<Job>>) : MetallicJobCreator =
            MetallicJobCreator(map)

    @Provides
    @IntoMap
    @StringKey(WatchListJob.TAG)
    fun watchListJob(spotifyAPI: SpotifyAPI) : Job = WatchListJob(spotifyAPI)
}