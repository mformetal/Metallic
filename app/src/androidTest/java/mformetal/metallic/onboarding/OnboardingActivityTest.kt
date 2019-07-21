package mformetal.metallic.onboarding

import androidx.lifecycle.Observer
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.support.test.espresso.IdlingPolicies
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.IdlingResource
import android.support.test.espresso.idling.CountingIdlingResource
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.Intents.*
import android.support.test.espresso.intent.matcher.IntentMatchers
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import androidx.core.content.ContextCompat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import assertk.assert
import assertk.assertions.*
import mformetal.metallic.R
import android.support.test.espresso.intent.matcher.IntentMatchers.*
import mformetal.metallic.home.HomeActivity
import mformetal.metallic.util.safeObserver
import mformetal.metallic.util.targetContext
import mformetal.metallic.util.testAppComponent
import org.junit.After
import org.junit.Before
import java.util.concurrent.TimeUnit


/**
 * Created by peelemil on 12/7/17.
 */
@RunWith(AndroidJUnit4::class)
class OnboardingActivityTest {

    @Rule
    @JvmField
    public val activityRule = ActivityTestRule<OnboardingActivity>(OnboardingActivity::class.java, true, false)
    private val activity : OnboardingActivity get() = activityRule.activity

    @Rule
    @JvmField
    public val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.READ_EXTERNAL_STORAGE)

    @Before
    fun setup() {
        Intents.init()
        testAppComponent.preferencesRepository().setHasOnboarded(false)

        activityRule.launchActivity(Intent(targetContext, OnboardingActivity::class.java))
    }

    @Test
    fun testActivityHasSplashBackground() {
        val windowBackground = activity.window.decorView.background
        assert(windowBackground is LayerDrawable)

        with (windowBackground as LayerDrawable) {
            val background = getDrawable(0)
            with (background) {
                assert(this).isInstanceOf(ColorDrawable::class.java)
                assert((this as ColorDrawable).color).isEqualTo(ContextCompat.getColor(activity, R.color.colorPrimary))
            }

            val icon = getDrawable(1)
            with (icon) {
                assert(this).isInstanceOf(BitmapDrawable::class.java)
                val bitmapDrawable = this as BitmapDrawable
                assert(bitmapDrawable.bitmap).isNotNull()

                val iconDrawable = BitmapFactory.decodeResource(activity.resources, R.mipmap.ic_launcher)
                assert(iconDrawable.width).isEqualTo(bitmapDrawable.bitmap.width)
                assert(iconDrawable.height).isEqualTo(bitmapDrawable.bitmap.height)
            }
        }
    }

    @Test
    fun testFinishingImportStartsHomeActivity() {
        IdlingPolicies.setMasterPolicyTimeout(2, TimeUnit.MINUTES)
        IdlingPolicies.setIdlingResourceTimeout(2, TimeUnit.MINUTES)
        val idlingResource = CountingIdlingResource("Waiting for import to finish")
        val observer = Observer<ImportStatus> {
            if (it == ImportStatus.FINISH) {
                idlingResource.decrement()
            }
        }

        activity.viewModel.observeImportStatusChanges().observeForever(observer)
        IdlingRegistry.getInstance().register(idlingResource)
        idlingResource.increment()

        intended(hasComponent(HomeActivity::class.qualifiedName))

        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @After
    fun tearDown() {
        Intents.release()
    }
}