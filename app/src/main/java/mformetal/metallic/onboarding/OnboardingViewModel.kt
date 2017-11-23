package mformetal.metallic.onboarding

import android.arch.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import mformetal.metallic.data.Artist
import mformetal.metallic.domain.api.spotify.SpotifyAPI
import javax.inject.Inject

/**
 * Created by mbpeele on 11/18/17.
 */
class OnboardingViewModel @Inject constructor(private val importer: PlayMusicImporter,
                                              private val spotifyAPI: SpotifyAPI) : ViewModel() {

    private val realm : Realm = Realm.getDefaultInstance()
    private var importDisposable : Disposable ?= null

    override fun onCleared() {
        super.onCleared()
        importDisposable?.dispose()
        realm.close()
    }

    fun observeArtists() : RealmResults<Artist> =
            realm.where(Artist::class.java).findAllSortedAsync("name", Sort.ASCENDING)

    fun import() {
        if (importDisposable == null) {
            importDisposable = importer.getArtists()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ artist ->
                        realm.executeTransactionAsync {
                            it.insertOrUpdate(artist)
                        }
                    })
        }
    }

    fun onArtistsSelected(collection: Collection<Artist>) {

    }
}