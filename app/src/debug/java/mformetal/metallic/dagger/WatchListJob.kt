package mformetal.metallic.dagger

import com.evernote.android.job.DailyJob
import com.evernote.android.job.Job
import com.evernote.android.job.JobRequest
import mformetal.metallic.domain.api.spotify.SpotifyAPI
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by peelemil on 12/1/17.
 */
class WatchListJob @Inject constructor(private val spotifyAPI: SpotifyAPI): Job() {

    companion object {
        const val TAG = "watchListJob"

        fun schedule() {
            JobRequest.Builder(TAG)
                    .startNow()
                    .build()
                    .schedule()
        }
    }

    override fun onRunJob(params: Params): Result {
        return Result.SUCCESS
    }
}