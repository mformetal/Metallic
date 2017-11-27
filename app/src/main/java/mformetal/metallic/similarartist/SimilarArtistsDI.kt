package mformetal.metallic.similarartist

import dagger.MembersInjector
import dagger.Module
import dagger.Subcomponent
import mformetal.metallic.dagger.ActivityScope

/**
 * @author - mbpeele on 11/25/17.
 */
@ActivityScope
@Subcomponent(modules = arrayOf(SimilarArtistModule::class))
interface SimilarArtistsComponent : MembersInjector<SimilarArtistsActivity>

@Module
class SimilarArtistModule