package mformetal.metallic.data

import io.reactivex.Completable

/**
 * @author - mbpeele on 12/6/17.
 */
interface ArtistRepository {

    fun saveArtist(artist: Artist) : Completable
}