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
    private val clarifyArtistData = MutableLiveData<List<Artist>>()
    private val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }

    fun observeSimilarArtists() : LiveData<List<Artist>> = similarArtistsData

    fun observeClarifyArtists() : LiveData<List<Artist>> = clarifyArtistData

    fun observeSearchError() : LiveData<Unit> = errorSimilarArtistsData

    fun onSimilarArtistClicked(artist: Artist) {
        // open Spotify or something? Idk
    }

    fun onClarifyingArtistClicked(artist: Artist) {
        val shouldUpdateWholeArtist = currentArtist.name != artist.name
        realm.executeTransaction {
            if (shouldUpdateWholeArtist) {
                currentArtist.deleteFromRealm()
                val newArtist = Artist(name = artist.name,
                        artworkUrl = artist.artworkUrl,
                        spofityId = artist.spofityId)
                currentArtist = it.copyToRealm(newArtist)
            } else {
                currentArtist.spofityId = artist.spofityId
            }
        }

        if (shouldUpdateWholeArtist) {
            spotifyAPI.getAlbumsofArtist(currentArtist.spofityId!!)
                    .subscribeOn(Schedulers.io())
                    .map {
                        it.items.map {
                            Album(name = it.name,
                                    artworkUrl = it.images.getOrNull(1)?.url ?: "",
                                    spofityId = it.id)
                        }
                    }
                    .flattenAsObservable { t -> t }
                    .flatMapSingle { album ->
                        spotifyAPI.getSongsOfAlbum(album.spofityId!!)
                                .map {
                                    album.apply {
                                        val songsList = it.items.map {
                                            Song(name = it.name,
                                                    spofityId = it.id)
                                        }
                                        songs = RealmList<Song>().apply { addAll(songsList) }
                                    }
                                }
                    }
                    .toList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMapCompletable { albums ->
                        Completable.create { emitter ->
                            realm.executeTransactionAsync({ realm ->
                                val localArtist = realm.where(Artist::class.java)
                                        .equalTo("name", artist.name)
                                        .findFirst()!!

                                localArtist.albums?.clear()
                                val realmAlbums = realm.copyToRealm(albums)

                                localArtist.albums = RealmList<Album>().apply { addAll(realmAlbums) }
                            }, {
                                emitter.onComplete()
                            }, { throwable ->
                                emitter.onError(throwable)
                            })
                        }
                    }
                    .subscribe()
        } else {
            searchForSimilarArtists(currentArtist)
        }
    }

    fun setCurrentArtist(name: String) {
        currentArtist = realm.where(Artist::class.java)
                .equalTo("name", name)
                .findFirst()!!
    }

    fun searchForSimilarArtists(artist: Artist) {
        val name = artist.name
        val spotifyId = artist.spofityId

        if (spotifyId == null) {
            spotifyAPI.searchArtist(artist.name!!)
                    .subscribeOn(Schedulers.io())
                    .delay(1, TimeUnit.SECONDS)
                    .flatMap {
                        val items = it.artists.items
                        if (items.size == 1) {
                            val newId = items[0].id
                            val localRealm = Realm.getDefaultInstance()
                            localRealm.executeTransaction {
                                val localArtist = it.where(Artist::class.java)
                                        .equalTo("name", name)
                                        .findFirst()!!
                                localArtist.spofityId = newId
                            }

                            spotifyAPI.searchSimilarArtists(newId)
                                    .zipWith(Single.just(true), BiFunction { t1, t2 ->
                                        Pair(t2, t1.artists.map {
                                            Artist(name = it.name,
                                                    spofityId = it.id,
                                                    artworkUrl = it.images[1].url)
                                        })
                                    })
                        } else {
                            val distinctItems = items.distinctBy { it.name }
                            val pair = Pair(false, distinctItems.map {
                                Artist(name = it.name,
                                        artworkUrl = it.images.getOrNull(1)?.url ?: "",
                                        spofityId = it.id)
                            })
                            Single.just(pair)
                        }
                    }
                    .flatMap {
                        if (it.second.isEmpty()) {
                            Single.error(IllegalStateException())
                        } else {
                            Single.just(it)
                        }
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it.first) {
                            similarArtistsData.value = it.second
                        } else {
                            clarifyArtistData.value = it.second
                        }
                    }, {
                        errorSimilarArtistsData.value = Unit
                    })
        } else {
            spotifyAPI.searchSimilarArtists(spotifyId)
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
                    .subscribe(Consumer {
                        similarArtistsData.value = it
                    })
        }
    }
}
