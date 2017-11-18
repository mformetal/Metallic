package mformetal.metallic.onboarding

import dagger.MembersInjector
import dagger.Module
import dagger.Subcomponent
import mformetal.metallic.dagger.ActivityScope

/**
 * Created by mbpeele on 11/18/17.
 */
@ActivityScope
@Subcomponent(modules = arrayOf(OnboardingActivityModule::class))
interface OnboardingActivityComponent : MembersInjector<OnboardingActivity>

@Module
class OnboardingActivityModule()