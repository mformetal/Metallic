package mformetal.metallic.home

import mformetal.metallic.data.Artist

/**
 * @author - mbpeele on 11/24/17.
 */
interface HomeAdapterClickDelegate {

    fun onArtistClickedForWatchList(artist: Artist)

}