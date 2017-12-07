package mformetal.metallic.onboarding

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.Disposables
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.realm.RealmList
import mformetal.metallic.data.Album
import mformetal.metallic.data.Artist
import mformetal.metallic.data.Song
import mformetal.metallic.util.RxCursorIterable
import java.util.concurrent.Callable
import javax.inject.Inject

/**
 * @author - mbpeele on 11/17/17.
 */
class PlayMusicImporter @Inject constructor(context: Context) : MusicImporter {

    private val contentResolver = context.applicationContext.contentResolver
    private val BASE_URI = Uri.parse("content://com.google.android.music.MusicContent")
    private val ARTISTS_URI = BASE_URI.buildUpon().appendPath("artists").build()
    private val ALBUMS_URI = BASE_URI.buildUpon().appendPath("album").build()
    private val SONGS_URI = BASE_URI.buildUpon().appendPath("audio").build()

    override fun getArtists() : Flowable<Artist> {
        return Flowable.using(
                {
                    contentResolver.query(ARTISTS_URI, arrayOf("artist", "artworkUrl"), null, null, null)
                }, { cursor ->
                    Flowable.fromIterable(RxCursorIterable(cursor))
                            .map { innerCursor ->
                                val artistName = innerCursor.getString(innerCursor.getColumnIndex("artist"))

                                val albums = getAlbums(artistName)
                                Artist(name = artistName,
                                        artworkUrl = innerCursor.getString(innerCursor.getColumnIndex("artworkUrl")),
                                        albums = albums)
                            }
                }, { cursor ->
                    cursor.close()
                }, true)
    }

    override fun getAlbums(artistName: String) : RealmList<Album> {
        val albums = RealmList<Album>()

        // Can't use selection here
        // Can't figure out the right URI to use
        // Results in stupid slow query, oh well
        val albumCursor = contentResolver.query(ALBUMS_URI,
                arrayOf("album_name", "album_artist", "album_art", "album_year"),
                null, null, null)

        albumCursor.use {
            while (!it.isClosed && it.moveToNext()) {
                val albumArtist = it.getString(it.getColumnIndex("album_artist"))
                if (albumArtist == artistName) {
                    val albumName = it.getString(it.getColumnIndex("album_name"))

                    val songs = getSongs(artistName, albumName)
                    val album = Album(name = albumName,
                            artworkUrl = it.getString(it.getColumnIndex("album_art")),
                            songs = songs,
                            yearReleased = it.getString(it.getColumnIndex("album_year")),
                            createdBy = artistName)
                    albums.add(album)
                }
            }
        }

        return albums
    }

    override fun getSongs(artistName: String, albumName: String) : RealmList<Song> {
        val songs = RealmList<Song>()

        val songsCursor = contentResolver.query(SONGS_URI,
                arrayOf("title"),
                "artist = ? AND album = ?",
                arrayOf(artistName, albumName), null)

        songsCursor.use {
            while (!it.isClosed && it.moveToNext()) {
                val songTitle = it.getString(it.getColumnIndex(MediaStore.Audio.Media.TITLE))

                val song = Song(name = songTitle,
                        onAlbum = albumName,
                        createdBy = artistName)
                songs.add(song)
            }
        }

        return songs
    }
}