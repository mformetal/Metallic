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
        val genres: List<String>,
        val id: String, //74Lk7WdgkYm19XoHvEsr5F
        val images: List<SpotifyImage>,
        val name: String, //Mouth Of The Architect
        val uri: String //spotify:artist:74Lk7WdgkYm19XoHvEsr5F
)

data class SpotifyImage(
		val height: Int, //640
		val url: String, //https://i.scdn.co/image/568d2c5ff8d7f1d8aa87394f7f55d4c9a112c524
		val width: Int //640
)

data class ArtistsSearchResultWrapper(
		val artists: ArtistsSearchResult
)

data class ArtistsSearchResult(
		val items: List<SpotifyItem>
)

data class SpotifyItem(
        val id: String, //52ue4x5xVjLx4cw2HEXMhi
        val images: List<SpotifyImage>,
        val name: String //Hey Rosetta!
)

data class AlbumsQueryResult(
		val items: List<SpotifyItem>
)

data class SongsQueryResult(
		val items: List<SpotifyItem>
)