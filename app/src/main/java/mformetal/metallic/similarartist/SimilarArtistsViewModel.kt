package mformetal.metallic.similarartist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import mformetal.metallic.data.Artist
import mformetal.metallic.domain.api.spotify.SpotifyAPI
import mformetal.metallic.util.SingleLiveData
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * @author - mbpeele on 11/25/17.
 */
class SimilarArtistsViewModel @Inject constructor(private val spotifyAPI: SpotifyAPI) : ViewModel() {

    lateinit var currentArtist : Artist

    private val realm = Realm.getDefaultInstance()
    private val similarArtistsData = MutableLiveData<List<Artist>>()
    private val errorSimilarArtistsData = SingleLiveData<Unit>()
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
        val name = artist.name
        val spotifyId = artist.spotifyId

        val single = if (spotifyId == null) {
            syncArtistThenSearch(artist, name)
        } else {
            search(spotifyId)
        }
        single.doOnSubscribe {
            compositeDisposable.add(it)
        }.subscribe({
            similarArtistsData.value = it
        }, {
            errorSimilarArtistsData.value = Unit
        })
    }

    private fun search(spotifyId: String) : Single<List<Artist>> {
        return spotifyAPI.searchSimilarArtists(spotifyId)
                .subscribeOn(Schedulers.io())
                .delay(1, TimeUnit.SECONDS)
                .map {
                    it.artists.map {
                        Artist(name = it.name,
                                spotifyId = it.id,
                                artworkUrl = it.images.getOrNull(1)?.url ?: "")
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun syncArtistThenSearch(artist: Artist, name: String?) : Single<List<Artist>> {
        return spotifyAPI.searchArtist(artist.name!!)
                .subscribeOn(Schedulers.io())
                .delay(1, TimeUnit.SECONDS)
                .flatMap {
                    val matchingItem = it.artists.items.find { it.name == name }
                    if (matchingItem == null) {
                        Single.error(NoSuchElementException())
                    } else {
                        val newId = matchingItem.id
                        val localRealm = Realm.getDefaultInstance()
                        localRealm.executeTransaction {
                            val localArtist = it.where(Artist::class.java)
                                    .equalTo("name", name)
                                    .findFirst()!!
                            localArtist.spotifyId = newId
                        }

                        search(newId)
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
    }
}

