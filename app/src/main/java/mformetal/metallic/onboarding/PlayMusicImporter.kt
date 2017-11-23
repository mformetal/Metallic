package mformetal.metallic.onboarding

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.Disposables
import io.realm.RealmList
import mformetal.metallic.data.Album
import mformetal.metallic.data.Artist
import mformetal.metallic.data.Song
import javax.inject.Inject

/**
 * @author - mbpeele on 11/17/17.
 */
class PlayMusicImporter @Inject constructor(context: Context) {

    private val contentResolver = context.applicationContext.contentResolver
    private val URI = Uri.parse("content://com.google.android.music.MusicContent/audio")

    fun getArtists() : Flowable<Artist> {
       return Flowable.create<Artist>({
           val alreadySeenNames = mutableSetOf<String>()

           val artistCursor = contentResolver.query(URI,
                    arrayOf(MediaStore.Audio.Artists.ARTIST),
                    null, null, null)

           it.setDisposable(Disposables.fromRunnable {
               artistCursor.close()
           })

           while (!artistCursor.isClosed && artistCursor.moveToNext()) {
               val artistName = artistCursor.getString(artistCursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST))
               if (!alreadySeenNames.contains(artistName)) {
                   alreadySeenNames.add(artistName)

                   val albums = getAlbums(artistName)
                   val artist = Artist(name = artistName, albums = albums)
                   it.onNext(artist)
               }
           }

           artistCursor.close()

           it.onComplete()
        }, BackpressureStrategy.LATEST)
    }

    private fun getAlbums(artistName: String) : RealmList<Album> {
        val albums = RealmList<Album>()

        val albumCursor = contentResolver.query(URI,
                arrayOf(MediaStore.Audio.Artists.Albums.ALBUM),
                MediaStore.Audio.Media.ARTIST + " = ? ",
                arrayOf(artistName), null)

        albumCursor.use {
            while (it.moveToNext() && !it.isClosed) {
                val albumName = it.getString(it.getColumnIndex(MediaStore.Audio.Albums.ALBUM))
                if (albums.none { it.name == albumName }) {
                    val songs = getSongs(artistName, albumName)
                    val album = Album(name = albumName, songs = songs)
                    albums.add(album)
                }
            }
        }

        albumCursor.close()

        return albums
    }

    private fun getSongs(artistName: String, albumName: String) : RealmList<Song> {
        val songs = RealmList<Song>()

        val songsCursor = contentResolver.query(URI,
                arrayOf(MediaStore.Audio.Media.TITLE),
                MediaStore.Audio.Media.ARTIST + " = ? AND " + MediaStore.Audio.Media.ALBUM + " = ?",
                arrayOf(artistName, albumName), null)

        songsCursor.use {
            while (it.moveToNext()) {
                val songTitle = it.getString(it.getColumnIndex(MediaStore.Audio.Media.TITLE))

                if (songs.none { it.name == songTitle }) {
                    val song = Song(name = songTitle)
                    songs.add(song)
                }
            }

        }

        return songs
    }
}