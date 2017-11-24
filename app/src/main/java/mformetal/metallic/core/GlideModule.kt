package mformetal.metallic.core

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import io.realm.log.LogLevel
import mformetal.metallic.BuildConfig

/**
 * @author - mbpeele on 11/23/17.
 */
@GlideModule
class GlideModule : AppGlideModule() {

    override fun applyOptions(context: Context?, builder: GlideBuilder) {
        if (BuildConfig.DEBUG) {
            builder.setLogLevel(LogLevel.DEBUG)
        }
    }
}