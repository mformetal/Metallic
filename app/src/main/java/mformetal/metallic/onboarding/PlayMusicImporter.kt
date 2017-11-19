package mformetal.metallic.onboarding

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.Disposables
import mformetal.metallic.domain.Album
import mformetal.metallic.domain.Artist
import mformetal.metallic.domain.Song
import javax.inject.Inject

/**
 * Created by mbpeele on 11/17/17.
 */
class PlayMusicImporter @Inject constructor(context: Context) {

    private val contentResolver = context.applicationContext.contentResolver
    private val URI = Uri.parse("content://com.google.android.music.MusicContent/audio")

    fun import() : Flowable<Artist> {
        return getArtists()
    }

    private fun getArtists() : Flowable<Artist> {
       return Flowable.create<Artist>({ emitter ->
            val artistCursor = contentResolver.query(URI,
                    arrayOf(MediaStore.Audio.Artists.ARTIST),
                    null, null, null)

            emitter.setDisposable(Disposables.fromRunnable {
                artistCursor.close()
            })

           while (artistCursor.moveToNext()) {
               val artistName = artistCursor.getString(artistCursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST))
               val artist = Artist(name = artistName, albums = listOf())
               emitter.onNext(artist)
           }

           emitter.onComplete()
        }, BackpressureStrategy.LATEST).distinctUntilChanged()
    }

    private fun getAlbums(artist: Artist) : List<Album> {
        val albums = mutableListOf<Album>()

        val albumCursor = contentResolver.query(URI,
                arrayOf(MediaStore.Audio.Artists.Albums.ALBUM),
                MediaStore.Audio.Media.ARTIST + " = ? ",
                arrayOf(artist.name), null)

        albumCursor.use {
            while (it.moveToNext()) {
                val albumName = it.getString(it.getColumnIndex(MediaStore.Audio.Albums.ALBUM))

                if (!albums.any { it.name == albumName }) {
                    val album = Album(name = albumName, songs = listOf())
                    albums.add(album)
                }
            }
        }

        return albums
    }

    private fun getSongs(artist: Artist, album: Album) : List<Song> {
        val songs = mutableListOf<Song>()

        val songsCursor = contentResolver.query(URI,
                arrayOf(MediaStore.Audio.Media.TITLE),
                MediaStore.Audio.Media.ARTIST + " = ? AND " + MediaStore.Audio.Media.ALBUM + " = ?",
                arrayOf(artist.name, album.name), null)

        songsCursor.use {
            while (it.moveToNext()) {
                val songTitle = it.getString(it.getColumnIndex(MediaStore.Audio.Media.TITLE))

                if (!songs.any { it.name == songTitle }) {
                    val song = Song(name = songTitle)
                    songs.add(song)
                }
            }
        }

        return songs
    }
}