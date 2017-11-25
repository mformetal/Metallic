package mformetal.metallic.artistdetail

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.graphics.Palette
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import mformetal.metallic.R
import mformetal.metallic.core.BaseActivity
import mformetal.metallic.core.GlideApp
import mformetal.metallic.data.Artist
import mformetal.metallic.util.ColorsUtils
import javax.inject.Inject


/**
 * @author - mbpeele on 11/25/17.
 */
class ArtistDetailActivity : BaseActivity() {

    @Inject
    lateinit var factory : ViewModelProvider.Factory

    lateinit var viewModel : ArtistDetailViewModel

    @BindView(R.id.artist_image) lateinit var image: ImageView
    @BindView(R.id.artist_name) lateinit var name: TextView
    @BindView(R.id.toolbar) lateinit var toolbar: Toolbar

    companion object {
        private const val KEY_ARTIST_NAME = "artistNameKey"

        fun create(context: Context, artist: Artist) : Intent {
            val intent = Intent(context, ArtistDetailActivity::class.java)
            intent.putExtra(KEY_ARTIST_NAME, artist.name)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.artist_detail)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)
        supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        app.component
                .artistDetail(ArtistDetailModule())
                .injectMembers(this)

        viewModel = ViewModelProviders.of(this)[ArtistDetailViewModel::class.java]

        val artist = viewModel.getArtistByName(intent.getStringExtra(KEY_ARTIST_NAME))

        name.text = artist.name

        GlideApp.with(this)
                .asBitmap()
                .load(artist.artworkUrl)
                .fitCenter()
                .listener(object : RequestListener<Bitmap> {
                    override fun onResourceReady(resource: Bitmap, model: Any?, target: Target<Bitmap>?,
                                                 dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        Palette.from(resource)
                                .maximumColorCount(3)
                                .clearFilters()
                                .generate { palette ->
                                    @ColorsUtils.Lightness val lightness = ColorsUtils.isDark(palette)
                                    val isDark = if (lightness == ColorsUtils.LIGHTNESS_UNKNOWN) {
                                        ColorsUtils.isDark(resource, resource.width / 2, 0)
                                    } else {
                                        lightness == ColorsUtils.IS_DARK
                                    }

                                    val navigationIcon = toolbar.navigationIcon!!.mutate()
                                    val currentColor = Color.BLACK
                                    val animatingColor = if (isDark) Color.WHITE else Color.BLACK
                                    if (animatingColor != currentColor) {
                                        val colorAnimator = ValueAnimator.ofArgb(currentColor, animatingColor)
                                        colorAnimator.duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
                                        colorAnimator.addUpdateListener {
                                            val color = it.animatedValue as Int
                                            navigationIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                                        }
                                        colorAnimator.start()
                                    }

                                    var statusBarColor = window.statusBarColor
                                    val topColor = ColorsUtils.getMostPopulousSwatch(palette)
                                    if (topColor != null && (isDark || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                                        statusBarColor = ColorsUtils.scrimify(topColor.rgb,
                                                isDark, .075f)

                                        if (!isDark && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            val flags = window.decorView.systemUiVisibility
                                            flags.or(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                                            window.decorView.systemUiVisibility = flags
                                        }
                                    }

                                    if (statusBarColor != window.statusBarColor) {
                                        val statusBarColorAnim = ObjectAnimator.ofArgb(window,
                                                "statusBarColor", window.statusBarColor, statusBarColor)
                                        statusBarColorAnim.duration = 1000
                                        statusBarColorAnim.interpolator = FastOutSlowInInterpolator()

                                        val textColorAnim = ObjectAnimator.ofArgb(name,
                                                "textColor", name.currentTextColor, statusBarColor)
                                        textColorAnim.duration = statusBarColorAnim.duration
                                        statusBarColorAnim.interpolator = FastOutSlowInInterpolator()

                                        AnimatorSet().apply {
                                            playTogether(statusBarColorAnim, textColorAnim)
                                            start()
                                        }
                                    }
                                }
                        return false
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?,
                                              isFirstResource: Boolean): Boolean {
                        return false
                    }
                })
                .into(image)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}