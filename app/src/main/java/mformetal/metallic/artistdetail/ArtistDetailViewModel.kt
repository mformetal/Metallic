package mformetal.metallic.artistdetail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import mformetal.metallic.data.Artist
import mformetal.metallic.domain.api.spotify.SpotifyAPI
import javax.inject.Inject

/**
 * @author - mbpeele on 11/25/17.
 */
class ArtistDetailViewModel @Inject constructor(private val spotifyAPI: SpotifyAPI) : ViewModel() {

    private val realm = Realm.getDefaultInstance()
    private val similarArtistsData = MutableLiveData<List<Artist>>()
    private val clarifyArtistData = MutableLiveData<List<Artist>>()
    private val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }

    fun onSimilarArtistClicked(artist: Artist) {

    }

    fun onClarifyingArtistClicked(artist: Artist) {

    }

    fun getArtistByName(name: String) : Artist {
        return realm.where(Artist::class.java)
                .equalTo("name", name)
                .findFirst()!!
    }

    fun observeSimilarArtists() : LiveData<List<Artist>> = similarArtistsData

    fun observeClarifyArtists() : LiveData<List<Artist>> = clarifyArtistData

    fun searchForSimilarArtists(artist: Artist) {
        if (artist.spofityId == null) {
            spotifyAPI.searchArtist(artist.name!!)
                    .subscribeOn(Schedulers.io())
                    .flatMap {
                        val items = it.artists.items
                        if (items.size == 1) {
                            val localRealm = Realm.getDefaultInstance()
                            val name = artist.name
                            localRealm.executeTransaction {
                                val localArtist = it.where(Artist::class.java)
                                        .equalTo("name", name)
                                        .findFirst()!!
                                localArtist.spofityId = items[0].id
                            }
                            spotifyAPI.searchSimilarArtists(artist.spofityId!!)
                                    .zipWith(Single.just(true), BiFunction { t1, t2 ->
                                        Pair(t2, t1.artists.map {
                                            Artist(name = it.name,
                                                    spofityId = it.id,
                                                    artworkUrl = it.images[1].url)
                                        })
                                    })
                        } else {
                            val pair = Pair(false, items.map {
                                Artist(name = it.name,
                                        spofityId = it.id,
                                        artworkUrl = it.images[1].url)
                            })
                            Single.just(pair)
                        }
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(Consumer {
                        if (it.first) {
                            similarArtistsData.value = it.second
                        } else {
                            clarifyArtistData.value = it.second
                        }
                    })
        } else {
            spotifyAPI.searchSimilarArtists(artist.spofityId!!)
                    .subscribeOn(Schedulers.io())
                    .map {
                        it.artists.map {
                            Artist(name = it.name,
                                    spofityId = it.id,
                                    artworkUrl = it.images[1].url)
                        }
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(Consumer {
                        similarArtistsData.value = it
                    })
        }
    }
}