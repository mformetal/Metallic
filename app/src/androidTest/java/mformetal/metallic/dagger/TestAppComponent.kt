package mformetal.metallic.dagger

import android.content.Context
import com.google.gson.Gson
import dagger.Component
import dagger.Module
import dagger.Provides
import mformetal.metallic.App
import mformetal.metallic.R
import mformetal.metallic.core.PreferencesRepository
import mformetal.metallic.core.SharedPreferencesRepository
import mformetal.metallic.data.ArtistRepository
import mformetal.metallic.data.RealmArtistRepository
import mformetal.metallic.domain.api.spotify.SpotifyAPI
import mformetal.metallic.domain.api.spotify.SpotifyAuthInterceptor
import mformetal.metallic.onboarding.MusicImporter
import mformetal.metallic.onboarding.PlayMusicImporter
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Created by peelemil on 12/7/17.
 */
@Singleton
@Component(modules = arrayOf(TestAppModule::class))
interface TestAppComponent : AppComponent {

    fun preferencesRepository() : PreferencesRepository

}

@Module(includes = arrayOf(ViewModelModule::class, JobsModule::class))
class TestAppModule(private val app: App) {

    @Provides
    fun app() : App = app

    @Provides
    fun context() : Context = app

    @Provides
    @Singleton
    fun preferencesRepository(context: Context) : PreferencesRepository {
        val preferences = context.getSharedPreferences("testPrefs", Context.MODE_PRIVATE)
        return SharedPreferencesRepository(preferences)
    }

    @Provides
    @Singleton
    fun gson() = Gson()

    @Singleton
    @Provides
    fun retrofit(gson: Gson, okHttpClientBuilder: OkHttpClient.Builder) : Retrofit.Builder {
        return Retrofit.Builder()
                .client(okHttpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    }

    @Provides
    @Singleton
    fun musicImporter() : MusicImporter = PlayMusicImporter(app)

    @Provides
    @Singleton
    fun artistRepository() : ArtistRepository = RealmArtistRepository()

    @Provides
    @Singleton
    fun okHttpBuilder() : OkHttpClient.Builder = OkHttpClient.Builder()

    @Provides
    @Singleton
    fun spotifyApi(retrofitBuilder: Retrofit.Builder,
                   okHttpClientBuilder: OkHttpClient.Builder,
                   context: Context) : SpotifyAPI {
        val client = okHttpClientBuilder
                .addInterceptor(SpotifyAuthInterceptor(
                        context.getString(R.string.spotify_client_id),
                        context.getString(R.string.spotify_client_secret)))
                .build()

        return retrofitBuilder
                .client(client)
                .baseUrl("https://api.spotify.com/v1/")
                .build()
                .create(SpotifyAPI::class.java)
    }
}