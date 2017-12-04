package mformetal.metallic.dagger

import com.evernote.android.job.DailyJob
import com.evernote.android.job.Job
import com.evernote.android.job.JobRequest
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.realm.Realm
import mformetal.metallic.data.Artist
import mformetal.metallic.domain.api.spotify.AlbumsQueryResult
import mformetal.metallic.domain.api.spotify.SpotifyAPI
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import android.content.Context.NOTIFICATION_SERVICE
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import io.realm.RealmList
import io.realm.RealmObject
import mformetal.metallic.BuildConfig
import mformetal.metallic.R
import mformetal.metallic.data.Album
import mformetal.metallic.data.NewArtist
import mformetal.metallic.home.HomeActivity
import mformetal.metallic.watchlist.WatchListActivity


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
        val realm = Realm.getDefaultInstance()
        val watchedArtists = realm.where(Artist::class.java)
                    .equalTo("isWatching", true)
                    .findAll()

        var result : Result ?= null

        Observable.fromIterable(watchedArtists)
                .filter {
                    it.spofityId != null
                }
                .flatMapSingle { artist ->
                    val query = spotifyAPI.getAlbumsofArtist(artist.spofityId!!).blockingGet()
                    Single.just(query to artist)
                }
                .map { (query, artist) ->
                    val newArtist = NewArtist(
                            name = artist.name,
                            spofityId = artist.spofityId,
                            artworkUrl = artist.artworkUrl,
                            albums = RealmList())
                    val albums = artist.albums ?: listOf<Album>()
                    val newAlbums = query.items
                            .distinctBy { it.name } // Spotify will return multiple records with the same name
                            .filter { item -> albums.none { it.name == item.name } }
                            .map {
                                Album(name = it.name,
                                        yearReleased = it.release_date,
                                        createdBy = artist.name,
                                        spofityId = it.id,
                                        artworkUrl = it.images.getOrNull(1)?.url ?: ""
                                )
                            }

                    newArtist.albums = RealmList<Album>().apply { addAll(newAlbums) }
                    newArtist
                }
                .toList()
                .doOnEvent { newArtists, throwable ->
                    if (throwable == null) {
                        result = Result.FAILURE
                    } else {
                        realm.use {
                            result = try {
                                it.delete(NewArtist::class.java)
                                it.insertOrUpdate(newArtists)
                                Result.SUCCESS
                            } catch (exception: Exception) {
                                Result.FAILURE
                            }
                        }
                    }
                }
                .blockingGet()

        val notificationBuilder = NotificationCompat.Builder(context, BuildConfig.APPLICATION_ID)
                .setSmallIcon(R.drawable.music_note_24dp)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.watch_list_notification_content))
        val resultIntent = Intent(context, WatchListActivity::class.java)

        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(HomeActivity::class.java)
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT)
        notificationBuilder.setContentIntent(resultPendingIntent)
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // mNotificationId is a unique integer your app uses to identify the
        // notification. For example, to cancel the notification, you can pass its ID
        // number to NotificationManager.cancel().
        mNotificationManager.notify(0, notificationBuilder.build())

        return result!!
    }
}