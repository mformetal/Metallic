package mformetal.metallic.onboarding

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import mformetal.metallic.core.PreferencesRepository
import mformetal.metallic.data.Artist
import mformetal.metallic.domain.api.spotify.SpotifyAPI
import mformetal.metallic.util.SingleLiveData
import mformetal.metallic.util.TimedLiveData
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by mbpeele on 11/18/17.
 */
class OnboardingViewModel @Inject constructor(private val importer: MusicImporter,
                                              private val spotifyAPI: SpotifyAPI,
                                              private val preferencesRepository: PreferencesRepository) : ViewModel() {

    private var importDisposable : Disposable ?= null
    private val importStatusLiveData = SingleLiveData<ImportStatus>()
    private val locallySavedArtistLiveData = TimedLiveData<Artist>(interval = 5, timeUnit = TimeUnit.SECONDS)

    val hasUserOnboarded : Boolean = preferencesRepository.hasUserOnboarded()

    override fun onCleared() {
        super.onCleared()
        importDisposable?.dispose()
    }

    fun observeImportStatusChanges() : LiveData<ImportStatus> {
        return importStatusLiveData
    }

    fun observeLocallySavedArtists() : LiveData<Artist> {
        return locallySavedArtistLiveData
    }

    fun import() {
        if (importDisposable == null) {
            importDisposable = importer.getArtists()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMapSingle { artist ->
                        Single.create<Artist> { emitter ->
                            val realm = Realm.getDefaultInstance()
                            realm.executeTransactionAsync({
                                it.insertOrUpdate(artist)
                            }, {
                                realm.close()
                                emitter.onSuccess(artist)
                            }, {
                                realm.close()
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
                    .subscribe {
                        locallySavedArtistLiveData.value = it
                    }
        }
    }
}