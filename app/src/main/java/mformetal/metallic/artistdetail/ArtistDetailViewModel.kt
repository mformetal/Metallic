package mformetal.metallic.artistdetail

import android.arch.lifecycle.ViewModel
import io.realm.Realm
import mformetal.metallic.data.Artist
import javax.inject.Inject

/**
 * @author - mbpeele on 11/25/17.
 */
class ArtistDetailViewModel @Inject constructor(): ViewModel() {

    private val realm : Realm = Realm.getDefaultInstance()

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }

    fun getArtistByName(name: String) : Artist {
        return realm.where(Artist::class.java)
                .equalTo("name", name)
                .findFirst()!!
    }
}