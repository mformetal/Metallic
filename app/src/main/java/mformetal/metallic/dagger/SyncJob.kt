package mformetal.metallic.dagger

import com.evernote.android.job.Job
import com.evernote.android.job.JobRequest
import io.reactivex.Observable
import io.realm.Realm
import mformetal.metallic.core.PreferencesRepository
import mformetal.metallic.data.Artist
import mformetal.metallic.domain.api.spotify.SpotifyAPI

/**
 * @author - mbpeele on 12/5/17.
 */
class SyncJob(private val spotifyAPI: SpotifyAPI,
              private val preferencesRepository: PreferencesRepository) : Job() {

    companion object {
        const val TAG = "syncJob"

        fun schedule() {
            JobRequest.Builder(TAG)
                    .startNow()
                    .build()
                    .schedule()
        }
    }

    override fun onRunJob(params: Params): Result {
        if (!preferencesRepository.hasUserOnboarded()) {
            return Result.RESCHEDULE
        }

        val realm = Realm.getDefaultInstance()
        val nonSyncedArtists = realm.where(Artist::class.java)
                .isNull("spotifyId")
                .findAll()

        Observable.fromIterable(nonSyncedArtists)
                .take(25)
                .flatMapSingle { artist ->
                    spotifyAPI.searchArtist(artist.name!!)
                            .doOnSuccess {
                                it.artists.items.find { it.name == artist.name }
                                        ?.let { matchingItem ->
                                            val newId = matchingItem.id
                                            realm.executeTransaction {
                                                val localArtist = it.where(Artist::class.java)
                                                        .equalTo("name", artist.name)
                                                        .findFirst()!!
                                                localArtist.spotifyId = newId
                                            }
                                        }
                            }
                }
                .blockingSubscribe()

        val remainingNonSyncArtists = realm.where(Artist::class.java)
                .isNull("spotifyId")
                .findAll()

        return if (remainingNonSyncArtists.size > 0) {
            Result.RESCHEDULE
        } else {
            Result.SUCCESS
        }.also {
            realm.close()
        }
    }
}