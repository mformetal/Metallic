package mformetal.metallic.home

import mformetal.metallic.data.Artist

/**
 * Created by peelemil on 11/30/17.
 */
interface HomeAdapterClickDelegate {

    fun addArtistToWatchList(artist: Artist)

    fun removeArtistFromWatchList(artist: Artist)
}