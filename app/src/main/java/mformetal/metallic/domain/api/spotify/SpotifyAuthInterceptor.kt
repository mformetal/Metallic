package mformetal.metallic.domain.api.spotify

import android.util.Base64
import com.google.gson.Gson
import okhttp3.*

/**
 * @author - mbpeele on 11/23/17.
 */
class SpotifyAuthInterceptor(private val clientId: String,
                             private val cliendSecret: String) : Interceptor {

    private var bearerToken : String = ""

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
                .newBuilder()
                .addHeader("Authorization", bearerToken)
                .build()

        val originalRequestResponse = chain.proceed(originalRequest)
        if (originalRequestResponse.code() == 401) {
            val authRequest = createAuthRequest()
            val authResponse = chain.proceed(authRequest)
            val authResult = Gson().fromJson<SpotifyAuthResult>(authResponse.body()!!.string(), SpotifyAuthResult::class.java)
            bearerToken = "${authResult.token_type} ${authResult.access_token}"
        }

        val newRequest = originalRequest
                .newBuilder()
                .addHeader("Authorization", bearerToken)
                .build()

        return chain.proceed(newRequest)
    }

    private fun createAuthRequest() : Request {
        val toEncode = "$clientId:$cliendSecret"
        return Request.Builder()
                .addHeader("Authorization",
                        "Basic ${Base64.encode(toEncode.toByteArray(), 0)}")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url("https://accounts.spotify.com/api/token")
                .post(RequestBody.create(
                        MediaType.parse("grant_type"), "client_credentials"))
                .build()
    }
}