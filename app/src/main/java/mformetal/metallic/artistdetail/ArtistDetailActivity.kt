package mformetal.metallic.artistdetail

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v4.view.ViewCompat
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.graphics.Palette
import android.support.v7.widget.Toolbar
import android.view.*
import android.view.animation.DecelerateInterpolator
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

        fun create(activity: Activity, imageView: ImageView, artist: Artist) : Pair<Intent, ActivityOptionsCompat> {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                    Pair(activity.findViewById(android.R.id.statusBarBackground), Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME),
                    Pair(activity.findViewById(android.R.id.navigationBarBackground), Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME),
                    Pair(imageView, ViewCompat.getTransitionName(imageView)))

            val intent = Intent(activity, ArtistDetailActivity::class.java)
            intent.putExtra(KEY_ARTIST_NAME, artist.name)
            return Pair(intent, options)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.artist_detail)
        ButterKnife.bind(this)

        postponeEnterTransition()

        val root = findViewById<ViewGroup>(android.R.id.content).getChildAt(0) as ViewGroup
        val viewTreeObserver = root.viewTreeObserver
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener  {
            override fun onPreDraw(): Boolean {
                if (viewTreeObserver.isAlive) {
                    viewTreeObserver.removeOnPreDrawListener(this)
                } else {
                    root.viewTreeObserver.removeOnPreDrawListener(this)
                }

                slideUpView(root,
                        resources.getInteger(android.R.integer.config_mediumAnimTime).toLong())

                return false
            }
        })

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

        ViewCompat.setTransitionName(image, artist.name)

        name.text = artist.name

        loadImage(artist)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun slideUpView(root: ViewGroup, duration: Long) {
        val viewGroup = root.getChildAt(1) as ViewGroup
        val offset = viewGroup.height / 2f
        for (i in 0 until viewGroup.childCount) {
            val view = viewGroup.getChildAt(i)
            view.translationY = offset
            view.alpha = 0f

            view.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(duration)
                    .setInterpolator(DecelerateInterpolator())
                    .setStartDelay((150 + 50 * i).toLong())
                    .start()
        }
    }

    private fun loadImage(artist: Artist) {
        GlideApp.with(this)
                .asBitmap()
                .load(artist.artworkUrl)
                .fitCenter()
                .listener(object : RequestListener<Bitmap> {
                    override fun onResourceReady(resource: Bitmap, model: Any?, target: Target<Bitmap>?,
                                                 dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        startPostponedEnterTransition()

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
                                            window.decorView.systemUiVisibility =
                                                    flags.or(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                                        }
                                    }

                                    if (statusBarColor != window.statusBarColor) {
                                        val statusBarColorAnim = ObjectAnimator.ofArgb(window,
                                                "statusBarColor", window.statusBarColor, statusBarColor)
                                        statusBarColorAnim.duration = 1000
                                        statusBarColorAnim.interpolator = FastOutSlowInInterpolator()
                                        statusBarColorAnim.start()
                                    }
                                }
                        return false
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?,
                                              isFirstResource: Boolean): Boolean {
                        startPostponedEnterTransition()
                        return false
                    }
                })
                .into(image)
    }
}