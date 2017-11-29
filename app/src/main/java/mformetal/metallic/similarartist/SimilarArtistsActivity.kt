package mformetal.metallic.similarartist

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v4.util.Pair
import android.support.v4.view.ViewCompat
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.app.AlertDialog
import android.support.v7.graphics.Palette
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.Toolbar
import android.transition.Transition
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
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
import mformetal.metallic.util.GridItemDecoration
import mformetal.metallic.util.safeObserver
import javax.inject.Inject

/**
 * @author - mbpeele on 11/25/17.
 */
class SimilarArtistsActivity : BaseActivity() {

    @Inject
    lateinit var factory : ViewModelProvider.Factory

    lateinit var viewModel : SimilarArtistsViewModel

    @BindView(R.id.artist_image) lateinit var image: ImageView
    @BindView(R.id.artist_name) lateinit var name: TextView
    @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
    @BindView(R.id.recycler) lateinit var recycler: RecyclerView
    @BindView(R.id.progress_bar) lateinit var progressBar: ProgressBar

    companion object {
        private const val KEY_ARTIST_NAME = "artistNameKey"

        fun create(activity: Activity, imageView: ImageView, artist: Artist) : Pair<Intent, ActivityOptionsCompat> {
            val onScreenNavigationBar = activity.findViewById<View>(android.R.id.navigationBarBackground)
            val statusBarPair = Pair(activity.findViewById<View>(android.R.id.statusBarBackground), Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME)
            val imageViewPair = Pair<View, String>(imageView, ViewCompat.getTransitionName(imageView))

            val options = if (onScreenNavigationBar == null) {
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        statusBarPair,
                        imageViewPair)
            } else {
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        Pair(onScreenNavigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME),
                        imageViewPair,
                        statusBarPair)
            }

            val intent = Intent(activity, SimilarArtistsActivity::class.java)
            intent.putExtra(KEY_ARTIST_NAME, artist.name)
            return Pair(intent, options)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.artist_detail)
        ButterKnife.bind(this)

        postponeEnterTransition()

        setSupportActionBar(toolbar)
        supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        app.component
                .artistDetail(SimilarArtistModule())
                .injectMembers(this)

        viewModel = ViewModelProviders.of(this, factory)[SimilarArtistsViewModel::class.java]

        viewModel.setCurrentArtist(intent.getStringExtra(KEY_ARTIST_NAME))

        val artist = viewModel.currentArtist

        ViewCompat.setTransitionName(image, artist.name)

        name.text = getString(R.string.artist_similar_to_current, artist.name)

        recycler.addItemDecoration(GridItemDecoration(resources.getDimensionPixelOffset(R.dimen.spacing_normal)))
        recycler.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        loadImage(artist)

        viewModel.searchForSimilarArtists(artist)

        viewModel.observeSearchError()
                .observe(this, Observer {
                    AlertDialog.Builder(this)
                            .setTitle(R.string.error_search_similar_artists_title)
                            .setMessage(getString(R.string.error_search_similar_artists_body, viewModel.currentArtist.name))
                            .setPositiveButton(android.R.string.ok, { dialogInterface, _ ->
                                dialogInterface.dismiss()
                                finish()
                            })
                            .create()
                            .show()

                    // set empty state for RecyclerView
                })

        viewModel.observeSimilarArtists()
                .observe(this, safeObserver {
                    progressBar.visibility = View.GONE

                    recycler.adapter = SimilarArtistsAdapter(it, object : SimilarArtistsAdapterClickDelegate {
                        override fun onArtistClicked(artist: Artist) {
                            viewModel.onSimilarArtistClicked(artist)
                        }
                    })
                })

        viewModel.observeClarifyArtists()
                .observe(this, safeObserver { artists ->
                    val items = arrayOfNulls<CharSequence>(artists.size)
                    for (i in artists.indices) {
                        val item = artists[i].name
                        items[i] = item
                    }

                    AlertDialog.Builder(this)
                            .setItems(items, { dialogInterface, index ->
                                dialogInterface.dismiss()

                                val clarifyingArtist = artists[index]
                                viewModel.onClarifyingArtistClicked(clarifyingArtist)
                            })
                            .setTitle(getString(R.string.clarify_artist_dialog_title, viewModel.currentArtist.name))
                            .setCancelable(false)
                            .create()
                            .show()
                })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
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

                                        window.sharedElementEnterTransition.addListener(object : Transition.TransitionListener {
                                            override fun onTransitionEnd(p0: Transition?) { }

                                            override fun onTransitionResume(p0: Transition?) { }

                                            override fun onTransitionPause(p0: Transition?) { }

                                            override fun onTransitionCancel(p0: Transition?) { }

                                            override fun onTransitionStart(p0: Transition?) {
                                                val accentColor = ContextCompat.getColor(this@SimilarArtistsActivity,
                                                        R.color.colorPrimary)
                                                val returnAnimation = ObjectAnimator.ofArgb(window,
                                                        "statusBarColor", statusBarColor, accentColor)
                                                returnAnimation.duration = 1000
                                                returnAnimation.interpolator = FastOutSlowInInterpolator()
                                                returnAnimation.start()
                                            }
                                        })
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