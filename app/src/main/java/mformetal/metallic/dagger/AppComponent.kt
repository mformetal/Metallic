package mformetal.metallic.dagger

import dagger.Component
import dagger.MembersInjector
import mformetal.metallic.App
import mformetal.metallic.onboarding.OnboardingActivityComponent
import mformetal.metallic.onboarding.OnboardingActivityModule
import javax.inject.Singleton

@Component(modules = arrayOf(AppModule::class))
@Singleton
interface AppComponent : MembersInjector<App> {

    fun onboarding(onboardingActivityModule: OnboardingActivityModule) : OnboardingActivityComponent

}