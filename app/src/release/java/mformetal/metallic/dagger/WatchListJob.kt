package mformetal.metallic.dagger

import com.evernote.android.job.Job
import mformetal.metallic.domain.api.spotify.SpotifyAPI
import javax.inject.Inject
import com.evernote.android.job.DailyJob
import com.evernote.android.job.DailyJob.DailyJobResult
import com.evernote.android.job.Job.Params



/**
 * Created by peelemil on 12/1/17.
 */
class WatchListJob @Inject constructor(private val spotifyAPI: SpotifyAPI): DailyJob() {

    companion object {
        const val TAG = "watchListJob"

        fun schedule() {
            DailyJob.schedule(JobRequest.Builder(TAG),
                    TimeUnit.HOURS.toMillis(1),
                    TimeUnit.HOURS.toMillis(6))
        }
    }

    override fun onRunDailyJob(params: Params): DailyJobResult {
        return DailyJobResult.SUCCESS
    }
}