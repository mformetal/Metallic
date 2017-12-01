package mformetal.metallic.domain.api.spotify

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @author - mbpeele on 11/20/17.
 */
interface SpotifyAPI {

    @GET("artists/{id}/related-artists")
    fun searchSimilarArtists(@Path("id") id: String) : Single<SimilarArtistsResult>

    @GET("search?type=artist")
    fun searchArtist(@Query("q") artistName: String) : Single<ArtistsSearchResultWrapper>

	@GET("albums/{id}")
	fun getAlbum(@Path("id") id: String) : Single<SpotifyItem>

	@GET("artists/{id}/albums")
	fun getAlbumsofArtist(@Path("id") id: String) : Single<AlbumsQueryResult>

	@GET("albums/{id}/tracks")
	fun getSongsOfAlbum(@Path("id") id: String) : Single<SongsQueryResult>
}

data class SpotifyAuthResult(
		val access_token: String,
		val token_type: String,
		val expires_in: Int
)

data class SimilarArtistsResult(
		val artists: List<SpotifyArtist>
)

data class SpotifyArtist(
        val id: String,
        val images: List<SpotifyImage>,
        val name: String,
        val uri: String
)

data class SpotifyImage(
		val height: Int,
		val url: String,
		val width: Int
)

data class ArtistsSearchResultWrapper(
		val artists: ArtistsSearchResult
)

data class ArtistsSearchResult(
		val items: List<SpotifyItem>
)

data class SpotifyItem(
        val id: String,
        val images: List<SpotifyImage>,
        val name: String,
		val release_date: String
)

data class AlbumsQueryResult(
		val items: List<SpotifyItem>
)

data class SongsQueryResult(
		val items: List<SpotifyItem>
)