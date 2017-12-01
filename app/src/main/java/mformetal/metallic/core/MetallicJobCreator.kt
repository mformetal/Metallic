package mformetal.metallic.core

import com.evernote.android.job.DailyJob
import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator
import com.evernote.android.job.JobRequest
import mformetal.metallic.BuildConfig
import mformetal.metallic.domain.api.spotify.SpotifyAPI
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Provider

/**
 * Created by peelemil on 11/30/17.
 */
class MetallicJobCreator @Inject constructor(private val map: Map<String, @JvmSuppressWildcards Provider<Job>>): JobCreator {

    override fun create(tag: String): Job? = map[tag]!!.get()
}