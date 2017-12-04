package mformetal.metallic.watchlist

import android.arch.lifecycle.ViewModel
import io.realm.Realm
import io.realm.RealmResults
import mformetal.metallic.data.NewArtist
import javax.inject.Inject

/**
 * Created by peelemil on 12/4/17.
 */
class WatchListViewModel @Inject constructor(): ViewModel() {

    private val realm = Realm.getDefaultInstance()

    val newArtists : RealmResults<NewArtist> = realm.where(NewArtist::class.java).findAllSortedAsync("name")

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}