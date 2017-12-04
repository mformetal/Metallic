package mformetal.metallic.watchlist

import dagger.MembersInjector
import dagger.Module
import dagger.Subcomponent
import mformetal.metallic.dagger.ActivityScope
import mformetal.metallic.similarartist.SimilarArtistsActivity

/**
 * Created by peelemil on 12/4/17.
 */
@ActivityScope
@Subcomponent(modules = arrayOf(WatchListModule::class))
interface WatchListComponent : MembersInjector<WatchListActivity>

@Module
class WatchListModule