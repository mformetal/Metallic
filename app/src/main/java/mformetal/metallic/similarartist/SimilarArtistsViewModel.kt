package mformetal.metallic.similarartist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmList
import mformetal.metallic.data.Album
import mformetal.metallic.data.Artist
import mformetal.metallic.data.Song
import mformetal.metallic.domain.api.spotify.SpotifyAPI
import mformetal.metallic.util.SingleLiveEvent
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * @author - mbpeele on 11/25/17.
 */
class SimilarArtistsViewModel @Inject constructor(private val spotifyAPI: SpotifyAPI) : ViewModel() {

    lateinit var currentArtist : Artist

    private val realm = Realm.getDefaultInstance()
    private val similarArtistsData = MutableLiveData<List<Artist>>()
    private val errorSimilarArtistsData = SingleLiveEvent<Unit>()
    private val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        realm.close()
        compositeDisposable.clear()
    }

    fun observeSimilarArtists() : LiveData<List<Artist>> = similarArtistsData

    fun observeSearchError() : LiveData<Unit> = errorSimilarArtistsData

    fun onSimilarArtistClicked(artist: Artist) {
        // open Spotify or something? Idk
    }

    fun setCurrentArtist(name: String) {
        currentArtist = realm.where(Artist::class.java)
                .equalTo("name", name)
                .findFirst()!!
    }

    fun searchForSimilarArtists(artist: Artist) {
        spotifyAPI.searchSimilarArtists(artist.spofityId!!)
                .subscribeOn(Schedulers.io())
                .delay(1, TimeUnit.SECONDS)
                .map {
                    it.artists.map {
                        Artist(name = it.name,
                                spofityId = it.id,
                                artworkUrl = it.images.getOrNull(1)?.url ?: "")
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    compositeDisposable.add(it)
                }
                .subscribe(Consumer {
                    similarArtistsData.value = it
                })
    }
}

