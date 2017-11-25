package mformetal.metallic.artistdetail

import dagger.MembersInjector
import dagger.Module
import dagger.Subcomponent
import mformetal.metallic.dagger.ActivityScope

/**
 * @author - mbpeele on 11/25/17.
 */
@ActivityScope
@Subcomponent(modules = arrayOf(ArtistDetailModule::class))
interface ArtistDetailComponent : MembersInjector<ArtistDetailActivity>

@Module
class ArtistDetailModule