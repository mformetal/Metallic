package mformetal.metallic.onboarding

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmList
import mformetal.metallic.core.PreferencesRepository
import mformetal.metallic.data.Album
import mformetal.metallic.data.Artist
import mformetal.metallic.data.Song
import mformetal.metallic.domain.api.spotify.SpotifyAPI
import mformetal.metallic.util.SingleLiveEvent
import javax.inject.Inject

/**
 * Created by mbpeele on 11/18/17.
 */
class OnboardingViewModel @Inject constructor(private val importer: MusicImporter,
                                              private val spotifyAPI: SpotifyAPI,
                                              private val preferencesRepository: PreferencesRepository) : ViewModel() {

    private var importDisposable : Disposable ?= null
    private val importStatusLiveData = SingleLiveEvent<ImportStatus>()

    val hasUserOnboarded : Boolean = preferencesRepository.hasUserOnboarded()

    override fun onCleared() {
        super.onCleared()
        importDisposable?.dispose()
    }

    fun observeImportStatusChanges() : LiveData<ImportStatus> {
        return importStatusLiveData
    }

    fun import() {
        if (importDisposable == null) {
            importDisposable = importer.getArtists()
                    .subscribeOn(Schedulers.io())
                    .flatMapSingle {
                        updateArtistFromSpotify(it)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMapCompletable { artists ->
                        Completable.create { emitter ->
                            val realm = Realm.getDefaultInstance()
                            realm.executeTransactionAsync({
                                it.insertOrUpdate(artists)
                            }, {
                                emitter.onComplete()
                            }, {
                                emitter.onError(it)
                            })
                        }
                    }
                    .doOnSubscribe {
                        importStatusLiveData.value = ImportStatus.START
                    }
                    .doOnComplete {
                        preferencesRepository.setHasOnboarded()
                        importStatusLiveData.value = ImportStatus.FINISH
                    }
                    .subscribe()
        }
    }

    private fun updateArtistFromSpotify(artist: Artist) : Single<Artist> {
        return spotifyAPI.searchArtist(artist.name!!)
                .flatMap {
                    val items = it.artists.items
                    if (items.isEmpty()) {
                        Single.just(artist)
                    } else {
                        val matchingItem = items.find { it.name == artist.name }
                        if (matchingItem == null) {
                            Single.just(artist)
                        } else {
                            val matchingId = matchingItem.id

                            val albumsWithSongs = spotifyAPI.getAlbumsofArtist(matchingId)
                                    .flattenAsObservable {
                                        it.items.map {
                                            Album(name = it.name,
                                                    artworkUrl = it.images.getOrNull(1)?.url ?: "",
                                                    spofityId = it.id)
                                        }
                                    }
                                    .filter { albumsFromSpotify ->
                                        artist.albums?.any { it.name == albumsFromSpotify.name } ?: false
                                    }
                                    .flatMapSingle { albumFromSpotify ->
                                        spotifyAPI.getSongsOfAlbum(albumFromSpotify.spofityId!!)
                                                .map {
                                                    albumFromSpotify.apply {
                                                        val songsList = it.items.map {
                                                            Song(name = it.name,
                                                                    spofityId = it.id)
                                                        }
                                                        songs = RealmList<Song>().apply { addAll(songsList) }
                                                    }
                                                }
                                    }
                                    .toList()
                                    .blockingGet()

                            Single.just(artist.apply {
                                spofityId = matchingId
                                albums = RealmList<Album>().apply { addAll(albumsWithSongs) }
                            })
                        }
                    }
                }
    }
}