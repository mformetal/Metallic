package mformetal.metallic.dagger

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import mformetal.metallic.App
import mformetal.metallic.domain.api.tastedive.TasteDiveAPI
import okhttp3.OkHttpClient
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
    fun retrofit(gson: Gson) : Retrofit {
        return Retrofit.Builder()
                .client(OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    @Provides
    @Singleton
    fun tasteDiveApi(retrofit: Retrofit) = retrofit.create(TasteDiveAPI::class.java)
}