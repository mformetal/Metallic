package mformetal.metallic.onboarding

import io.reactivex.Flowable
import io.realm.RealmList
import mformetal.metallic.data.Album
import mformetal.metallic.data.Artist
import mformetal.metallic.data.Song

/**
 * @author - mbpeele on 12/6/17.
 */
interface MusicImporter {

    fun getArtists() : Flowable<Artist>

    fun getAlbums(artistName: String) : RealmList<Album>

    fun getSongs(artistName: String, albumName: String) : RealmList<Song>
}