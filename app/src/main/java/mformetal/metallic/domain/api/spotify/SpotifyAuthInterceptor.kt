package mformetal.metallic.domain.api.spotify

import android.util.Base64
import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * @author - mbpeele on 11/23/17.
 */
class SpotifyAuthInterceptor(private val clientId: String,
                             private val cliendSecret: String) : Interceptor {

    private var bearerToken : String = ""

    override fun intercept(chain: Interceptor.Chain): Response {
        if (bearerToken.isEmpty()) {
            val authRequest = createAuthRequest()
            val authResponse = chain.proceed(authRequest)
            val authResult = Gson().fromJson<SpotifyAuthResult>(authResponse.body!!.string(), SpotifyAuthResult::class.java)
            bearerToken = "${authResult.token_type} ${authResult.access_token}"
        }

        val newRequest = chain.request()
                .newBuilder()
                .addHeader("Authorization", bearerToken)
                .build()

        return chain.proceed(newRequest)
    }

    private fun createAuthRequest() : Request {
        val toEncode = "$clientId:$cliendSecret"
        val bytes = Base64.encode(toEncode.toByteArray(), Base64.NO_WRAP or Base64.URL_SAFE)
        return Request.Builder()
                .addHeader("Authorization", "Basic ${String(bytes)}")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url("https://accounts.spotify.com/api/token")
                .post(FormBody.Builder()
                        .add("grant_type", "client_credentials")
                        .build()
                )
                .build()
    }
}