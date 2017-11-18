package mformetal.metallic.onboarding

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by mbpeele on 11/18/17.
 */
class OnboardingViewModel @Inject constructor(private val importer: PlayMusicImporter) : ViewModel() {

    private var importDisposable : Disposable ?= null
    private val artists = MutableLiveData<List<String>>()

    override fun onCleared() {
        super.onCleared()
        importDisposable?.dispose()
    }

    fun observeArtists() : LiveData<List<String>> {
        if (importDisposable == null) {
            importDisposable = importer.import()
                    .subscribeOn(Schedulers.io())
                    .subscribe {
                        val value = if (artists.value == null) mutableListOf() else artists.value!!
                        artists.postValue(value + it.name)
                    }
        }

        return artists
    }
}