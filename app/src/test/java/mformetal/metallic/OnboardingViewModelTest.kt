package mformetal.metallic

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import assertk.assert
import assertk.assertions.isEqualTo
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Completable
import io.reactivex.Flowable
import mformetal.metallic.core.PreferencesRepository
import mformetal.metallic.data.Artist
import mformetal.metallic.data.ArtistRepository
import mformetal.metallic.onboarding.ImportStatus
import mformetal.metallic.onboarding.MusicImporter
import mformetal.metallic.onboarding.OnboardingViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


/**
 * @author - mbpeele on 12/6/17.
 */
@RunWith(JUnit4::class)
class OnboardingViewModelTest {

    @Suppress("RedundantVisibilityModifier")
    @Rule
    @JvmField
    public val instantExecutor = InstantTaskExecutorRule()
    @Suppress("RedundantVisibilityModifier")
    @Rule
    @JvmField
    public val schedulerRule = SchedulerRule()

    val importer : MusicImporter = mock()
    val artistRepository : ArtistRepository = mock()
    val preferencesRepository : PreferencesRepository = mock()
    lateinit var viewModel : OnboardingViewModel

    @Before
    fun setup() {
        viewModel = OnboardingViewModel(importer, artistRepository, preferencesRepository)
    }

    @Test
    fun viewModelJustCallsThroughToRepository() {
        whenever(preferencesRepository.hasUserOnboarded()).thenReturn(true)
        assert(viewModel.hasUserOnboarded).isEqualTo(preferencesRepository.hasUserOnboarded())
    }

    @Test
    fun callingImportObservesImporterSource() {
        whenever(importer.getArtists())
                .thenReturn(Flowable.never())

        viewModel.importDisposable = null
        viewModel.import()
        verify(importer).getArtists()
        viewModel.importDisposable = null
    }

    @Test
    fun callingImportDoesntRestartProcess() {
        whenever(importer.getArtists())
                .thenReturn(Flowable.never())
        viewModel.importDisposable = null
        viewModel.import()
        viewModel.import()
        verify(importer, times(1)).getArtists()
        viewModel.importDisposable = null
    }

    @Test
    fun observingEmittedArtists() {
        whenever(importer.getArtists())
                .thenReturn(Flowable.just(
                        Artist(name = "first")
                ))

        whenever(artistRepository.saveArtist(any()))
                .thenReturn(Completable.complete())

        val artistObserver = mock<Observer<Artist>>()
        val statusObserver = mock<Observer<ImportStatus>>()

        viewModel.observeImportStatusChanges().observeForever(statusObserver)
        viewModel.observeLocallySavedArtists().observeForever(artistObserver)

        viewModel.import()

        verify(artistObserver).onChanged(any())
    }

    @Test
    fun artistEmittedFromImporterIsSaved() {
        whenever(importer.getArtists())
                .thenReturn(Flowable.just(Artist(name = "first")))

        val captor = argumentCaptor<Artist>()

        whenever(artistRepository.saveArtist(captor.capture()))
                .thenReturn(Completable.complete())

        viewModel.import()

        captor.lastValue.let {
            assert(it.name).isEqualTo("first")
        }
    }
}