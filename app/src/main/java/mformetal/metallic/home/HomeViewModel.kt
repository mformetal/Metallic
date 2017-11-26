package mformetal.metallic.home

import android.arch.lifecycle.ViewModel
import io.realm.Case
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import mformetal.metallic.data.Artist
import javax.inject.Inject

/**
 * @author - mbpeele on 11/23/17.
 */
class HomeViewModel @Inject constructor(): ViewModel(), HomeAdapterClickDelegate {

    private val realm : Realm = Realm.getDefaultInstance()

    val artists : RealmResults<Artist> = realm.where(Artist::class.java).findAllSortedAsync("name", Sort.ASCENDING)

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }

    fun searchArtistsByName(query: String) : RealmResults<Artist> {
        return realm.where(Artist::class.java)
                .contains("name", query, Case.INSENSITIVE)
                .findAllSortedAsync("name", Sort.ASCENDING)
    }

    override fun onArtistClickedForWatchList(artist: Artist) {
        if (!artist.isWatching) {
            realm.executeTransaction {
                artist.isWatching = true
            }
        }
    }
}