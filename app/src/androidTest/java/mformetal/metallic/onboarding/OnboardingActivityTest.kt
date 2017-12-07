package mformetal.metallic.onboarding

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.support.test.espresso.intent.Intents
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v4.content.ContextCompat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import assertk.assert
import assertk.assertions.*
import mformetal.metallic.R
import mformetal.metallic.util.targetContext
import mformetal.metallic.util.testAppComponent
import org.junit.Before


/**
 * Created by peelemil on 12/7/17.
 */
@RunWith(AndroidJUnit4::class)
class OnboardingActivityTest {

    @Rule
    @JvmField
    public val activityRule = ActivityTestRule<OnboardingActivity>(OnboardingActivity::class.java, true, false)
    private val activity : OnboardingActivity get() = activityRule.activity

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
        }
    }
}