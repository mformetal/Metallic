package mformetal.metallic.domain.api.tastedive

import io.reactivex.Single
import retrofit2.http.Query

/**
 * Created by mbpeele on 11/18/17.
 */
interface TasteDiveAPI {

    fun searchForSimilarArtists(@Query("q") artist: String) : Single<TasteDiveSearchResult>
}

data class TasteDiveSearchResult(val similar: Similar)

data class Similar(val results: List<Result>)

data class Result(val name: String)