package mformetal.metallic.domain.api.spotify

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by mbpeele on 11/20/17.
 */
interface SpotifyAPI {

    @GET("recommendations")
    fun searchForSimilarArtists(@Query("seedArtists") artistSpotifyIds: List<String>)
}