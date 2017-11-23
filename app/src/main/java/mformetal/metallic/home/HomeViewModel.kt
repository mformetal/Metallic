package mformetal.metallic.home

import android.arch.lifecycle.ViewModel
import io.realm.Case
import io.realm.Realm
import io.realm.RealmResults
import mformetal.metallic.data.Artist

/**
 * @author - mbpeele on 11/23/17.
 */
class HomeViewModel : ViewModel() {

    private val realm : Realm = Realm.getDefaultInstance()

    val artists : RealmResults<Artist>
        get() = realm.where(Artist::class.java).findAllAsync()

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }

    fun searchArtistsByName(query: String) : RealmResults<Artist> {
        return realm.where(Artist::class.java)
                .contains("name", query, Case.INSENSITIVE)
                .findAllAsync()
    }
}