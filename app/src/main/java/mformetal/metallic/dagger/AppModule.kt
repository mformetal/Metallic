package mformetal.metallic.dagger

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import mformetal.metallic.App
import mformetal.metallic.BuildConfig
import mformetal.metallic.domain.api.spotify.SpotifyAPI
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = arrayOf(ViewModelModule::class))
class AppModule(private val app: App) {

    @Provides
    fun app() : App = app

    @Provides
    fun context() : Context = app

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
    fun okHttpBuilder() : OkHttpClient.Builder {
        val builder =  OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }
        return builder
    }

    @Provides
    @Singleton
    fun spotifyApi(retrofitBuilder: Retrofit.Builder,
                   okHttpClientBuilder: OkHttpClient.Builder) : SpotifyAPI {
        return retrofitBuilder
                .client(okHttpClientBuilder.build())
                .baseUrl("https://tastedive.com/api/")
                .build()
                .create(SpotifyAPI::class.java)
    }
}