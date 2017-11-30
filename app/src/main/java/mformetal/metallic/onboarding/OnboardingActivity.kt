package mformetal.metallic.onboarding

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import mformetal.metallic.core.BaseActivity
import mformetal.metallic.home.HomeActivity
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

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            viewModel.import()
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_PERMISSION_EXTERNAL_STORAGE)
        }

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
}