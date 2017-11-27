package mformetal.metallic.dagger

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import mformetal.metallic.similarartist.SimilarArtistsViewModel
import mformetal.metallic.home.HomeViewModel
import mformetal.metallic.onboarding.OnboardingViewModel

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
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}