package mformetal.metallic.onboarding

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiConsumer
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import mformetal.metallic.domain.Artist
import javax.inject.Inject

/**
 * Created by mbpeele on 11/18/17.
 */
class OnboardingViewModel @Inject constructor(private val importer: PlayMusicImporter) : ViewModel() {

    private var importDisposable : Disposable ?= null
    private val liveData = MutableLiveData<List<Artist>>()

    override fun onCleared() {
        super.onCleared()
        importDisposable?.dispose()
    }

    fun observeArtists() : LiveData<List<Artist>> {
        if (importDisposable == null) {
            importDisposable = importer.getBasicArtistInfo()
                    .subscribeOn(Schedulers.io())
                    .collectInto(mutableListOf(), BiConsumer<MutableList<Artist>, Artist> {
                        t1, t2 -> t1.add(t2)
                    })
                    .subscribe(Consumer<List<Artist>> {
                        val list = it.asSequence().sortedBy { it.name.toUpperCase() }.distinctBy { it.name }.toList()
                        liveData.postValue(list)
                    })
        }

        return liveData
    }
}