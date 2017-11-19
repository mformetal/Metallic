package mformetal.metallic.onboarding

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import mformetal.metallic.domain.Artist
import javax.inject.Inject

/**
 * Created by mbpeele on 11/18/17.
 */
class OnboardingViewModel @Inject constructor(private val importer: PlayMusicImporter) : ViewModel() {

    private var importDisposable : Disposable ?= null
    private val artists = MutableLiveData<Artist>()

    override fun onCleared() {
        super.onCleared()
        importDisposable?.dispose()
    }

    fun observeArtists() : LiveData<Artist> {
        if (importDisposable == null) {
            importDisposable = importer.import()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        artists.value = it
                    }
        }

        return artists
    }
}