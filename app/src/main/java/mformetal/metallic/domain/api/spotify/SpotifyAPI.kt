package mformetal.metallic.domain.api.spotify

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by mbpeele on 11/20/17.
 */
interface SpotifyAPI {

    @POST
    fun authorize(@Header("Authorization") auth: String) : Single<Response<SpotifyAuthResult>>

    @GET("recommendations")
    fun searchForSimilarArtists(@Query("seedArtists") artistSpotifyIds: List<String>)

    @GET("search?type=artist")
    fun searchArtist(@Query("q") artistName: String) : Single<Response<SpotifyArtistsSearchResult>>
}

data class SpotifyAuthResult(
		val access_token: String,
		val token_type: String,
		val expires_in: Int
)

data class SpotifyArtistsSearchResult(
		val artists: SpotifySearchResult
)

data class SpotifySearchResult(
        val href: String,
        val items: List<SpotifyItem>,
        val limit: Int,
        val next: Any,
        val offset: Int,
        val previous: Any,
        val total: Int
)

data class SpotifyItem(
        val external_urls: ExternalUrls,
        val genres: List<String>,
        val href: String,
        val id: String,
        val images: List<SpotifyImage>,
        val name: String,
        val popularity: Int,
        val type: String,
        val uri: String
)

data class ExternalUrls(
		val spotify: String
)

data class SpotifyImage(
		val height: Int,
		val url: String,
		val width: Int
)