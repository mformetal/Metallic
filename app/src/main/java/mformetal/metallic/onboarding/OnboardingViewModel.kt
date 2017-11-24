package mformetal.metallic.onboarding

import android.arch.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import mformetal.metallic.core.PreferencesRepository
import mformetal.metallic.core.Router
import mformetal.metallic.data.Artist
import javax.inject.Inject

/**
 * Created by mbpeele on 11/18/17.
 */
class OnboardingViewModel @Inject constructor(private val importer: MusicImporter,
                                              private val router: Router,
                                              private val preferencesRepository: PreferencesRepository) : ViewModel() {

    private var importDisposable : Disposable ?= null

    val hasUserOnboarded : Boolean = preferencesRepository.hasUserOnboarded()

    override fun onCleared() {
        super.onCleared()
        importDisposable?.dispose()
    }

    fun import() {
        if (importDisposable == null) {
            importDisposable = importer.getArtists()
                    .subscribeOn(Schedulers.io())
                    .collectInto(mutableListOf<Artist>(), { t1, t2 ->
                        t1.add(t2)
                    })
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
                    .subscribe {
                        onboard()
                    }
        }
    }

    fun onboard() {
        preferencesRepository.setHasOnboarded()
        router.onboard()
    }
}