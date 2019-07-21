package mformetal.metallic.onboarding

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.palette.graphics.Palette
import android.transition.Transition
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import mformetal.metallic.R
import mformetal.metallic.core.BaseActivity
import mformetal.metallic.core.GlideApp
import mformetal.metallic.data.Artist
import mformetal.metallic.home.HomeActivity
import mformetal.metallic.util.ColorsUtils
import mformetal.metallic.util.safeObserver
import javax.inject.Inject

/**
 * @author - mbpeele on 11/17/17.
 */
class OnboardingActivity : BaseActivity() {

    private val REQUEST_PERMISSION_EXTERNAL_STORAGE = 1

    @Inject
    lateinit var factory : ViewModelProvider.Factory

    lateinit var viewModel : OnboardingViewModel

    @BindView(R.id.current_artist_image)
    lateinit var imageView : ImageView
    @BindView(R.id.current_artist_name)
    lateinit var textView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app.component
                .onboarding(OnboardingActivityModule())
                .injectMembers(this)

        viewModel = ViewModelProviders.of(this, factory)[OnboardingViewModel::class.java]

        if (viewModel.hasUserOnboarded) {
            startHomeActivity()
            return
        }

        setContentView(R.layout.onboarding)
        ButterKnife.bind(this)

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            grantUriPermission(packageName,
                    Uri.parse("content://com.google.android.music.MusicContent"),
                    Intent.FLAG_GRANT_READ_URI_PERMISSION)

            viewModel.import()
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_PERMISSION_EXTERNAL_STORAGE)
        }

        viewModel.observeLocallySavedArtists()
                .observe(this, safeObserver {
                    showArtistPreview(it)
                })

        viewModel.observeImportStatusChanges()
                .observe(this, safeObserver {
                    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
                    when (it) {
                        ImportStatus.START -> {
                            // Can't seem to figure out if it's possible to animate
                            // findViewById(R.id.content) or
                            // window.decorView
                        }
                        ImportStatus.FINISH -> {
                            startHomeActivity()
                        }
                    }
                })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.import()
                } else {
                    finish()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun startHomeActivity() {
        val intent = HomeActivity.create(this)
        startActivity(intent)
        finish()
    }

    private fun showArtistPreview(artist: Artist) {
        textView.text = artist.name

        GlideApp.with(this)
                .asBitmap()
                .load(artist.artworkUrl)
                .fitCenter()
                .transition(BitmapTransitionOptions.withCrossFade())
                .listener(object : RequestListener<Bitmap> {
                    override fun onResourceReady(resource: Bitmap, model: Any?, target: Target<Bitmap>?,
                                                 dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        androidx.palette.graphics.Palette.from(resource)
                                .maximumColorCount(3)
                                .clearFilters()
                                .generate { palette ->
                                    if (palette == null) return@generate
                                    
                                    val decorView = window.decorView.background
                                    if (decorView is LayerDrawable) {
                                        val launcherIcon = decorView.findDrawableByLayerId(R.id.icon)
                                        if (launcherIcon.alpha == 255) {
                                            val alphaAnim = ValueAnimator.ofInt(launcherIcon.alpha, 0)
                                            alphaAnim.duration = 600
                                            alphaAnim.interpolator = FastOutSlowInInterpolator()
                                            alphaAnim.addUpdateListener {
                                                launcherIcon.alpha = it.animatedValue as Int
                                            }
                                            alphaAnim.start()
                                        }
                                    }
                                    @ColorsUtils.Lightness val lightness = ColorsUtils.isDark(palette)
                                    val isDark = if (lightness == ColorsUtils.LIGHTNESS_UNKNOWN) {
                                        ColorsUtils.isDark(resource, resource.width / 2, 0)
                                    } else {
                                        lightness == ColorsUtils.IS_DARK
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
                                                val accentColor = ContextCompat.getColor(this@OnboardingActivity,
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
                        return false
                    }
                })
                .into(imageView)
    }
}