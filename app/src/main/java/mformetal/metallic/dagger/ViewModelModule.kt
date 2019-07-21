package mformetal.metallic.dagger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import mformetal.metallic.similarartist.SimilarArtistsViewModel
import mformetal.metallic.home.HomeViewModel
import mformetal.metallic.onboarding.OnboardingViewModel
import mformetal.metallic.watchlist.WatchListViewModel

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(OnboardingViewModel::class)
    abstract fun bindOnboardingViewModel(onboardingViewModel: OnboardingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SimilarArtistsViewModel::class)
    abstract fun bindArtistDetailViewModel(similarArtistsViewModel: SimilarArtistsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WatchListViewModel::class)
    abstract fun bindWatchListViewModel(watchListViewModel: WatchListViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}