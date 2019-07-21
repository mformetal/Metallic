package mformetal.metallic.home

import androidx.lifecycle.ViewModel
import io.realm.Case
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import mformetal.metallic.data.Album
import mformetal.metallic.data.Artist
import mformetal.metallic.data.Song
import javax.inject.Inject

/**
 * @author - mbpeele on 11/23/17.
 */
class HomeViewModel @Inject constructor(): ViewModel() {

    private val realm : Realm = Realm.getDefaultInstance()

    val artists : RealmResults<Artist> = realm.where(Artist::class.java).findAllAsync().sort("name", Sort.ASCENDING)
    val albums : RealmResults<Album> = realm.where(Album::class.java).findAllAsync().sort("name", Sort.ASCENDING)
    val songs : RealmResults<Song> = realm.where(Song::class.java).findAllAsync().sort("name", Sort.ASCENDING)

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }

    fun searchArtistsByName(query: String) : RealmResults<Artist> {
        return realm.where(Artist::class.java)
                .contains("name", query, Case.INSENSITIVE)
                .findAllAsync()
                .sort("name", Sort.ASCENDING)
    }

    fun searchAlbumsByName(query: String) : RealmResults<Album> {
        return realm.where(Album::class.java)
                .contains("name", query, Case.INSENSITIVE)
                .findAllAsync()
                .sort("name", Sort.ASCENDING)
    }

    fun searchSongsByName(query: String) : RealmResults<Song> {
        return realm.where(Song::class.java)
                .contains("name", query, Case.INSENSITIVE)
                .findAllAsync()
                .sort("name", Sort.ASCENDING)
    }

    fun addArtistToWatchList(artist: Artist) {
        if (!artist.isWatching) {
            realm.executeTransaction {
                artist.isWatching = true
            }
        }
    }

    fun removeArtistFromWatchList(artist: Artist) {
        if (artist.isWatching) {
            realm.executeTransaction {
                artist.isWatching = false
            }
        }
    }
}