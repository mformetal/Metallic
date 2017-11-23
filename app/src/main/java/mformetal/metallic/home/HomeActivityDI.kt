package mformetal.metallic.home

import dagger.MembersInjector
import dagger.Module
import dagger.Subcomponent
import mformetal.metallic.dagger.ActivityScope

/**
 * @author - mbpeele on 11/23/17.
 */
@ActivityScope
@Subcomponent(modules = arrayOf(HomeModule::class))
interface HomeActivityComponent : MembersInjector<HomeActivity>

@Module
class HomeModule()