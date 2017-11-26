package mformetal.metallic.onboarding

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import butterknife.BindView
import butterknife.ButterKnife
import mformetal.metallic.R
import mformetal.metallic.core.BaseActivity
import mformetal.metallic.home.HomeActivity
import javax.inject.Inject

/**
 * @author - mbpeele on 11/17/17.
 */
class OnboardingActivity : BaseActivity() {

    private val REQUEST_PERMISSION_EXTERNAL_STORAGE = 1

    @BindView(R.id.animator) lateinit var animator : ImageView

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

        setContentView(R.layout.onboarding)
        ButterKnife.bind(this)

        viewModel.observeImportStatusChanges()
                .observe(this, Observer {
                    if (it!!) {
                        val intent = HomeActivity.create(this)
                        startActivity(intent)
                    }
                })

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                 == PackageManager.PERMISSION_GRANTED) {
            viewModel.import()
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_PERMISSION_EXTERNAL_STORAGE)
        }
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