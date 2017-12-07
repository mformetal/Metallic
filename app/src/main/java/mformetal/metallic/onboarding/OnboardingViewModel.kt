package mformetal.metallic.onboarding

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.support.annotation.VisibleForTesting
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import mformetal.metallic.core.PreferencesRepository
import mformetal.metallic.data.Artist
import mformetal.metallic.data.ArtistRepository
import mformetal.metallic.util.SingleLiveData
import mformetal.metallic.util.TimedLiveData
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by mbpeele on 11/18/17.
 */
class OnboardingViewModel @Inject constructor(private val importer: MusicImporter,
                                              private val artistRepository: ArtistRepository,
                                              private val preferencesRepository: PreferencesRepository) : ViewModel() {

    @VisibleForTesting
    var importDisposable : Disposable ?= null
    private val importStatusLiveData = SingleLiveData<ImportStatus>()
    private val locallySavedArtistLiveData = TimedLiveData<Artist>(interval = 5, timeUnit = TimeUnit.SECONDS)

    val hasUserOnboarded : Boolean
        get() = preferencesRepository.hasUserOnboarded()

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
                        artistRepository.saveArtist(artist)
                                .toSingleDefault(artist)
                    }
                    .doOnSubscribe {
                        importStatusLiveData.postValue(ImportStatus.START)
                    }
                    .doOnComplete {
                        preferencesRepository.setHasOnboarded()
                        importStatusLiveData.postValue(ImportStatus.FINISH)
                    }
                    .subscribe {
                        locallySavedArtistLiveData.postValue(it)
                    }
        }
    }
}