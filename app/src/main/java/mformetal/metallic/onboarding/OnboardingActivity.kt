package mformetal.metallic.onboarding

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import mformetal.metallic.R
import mformetal.metallic.core.BaseActivity
import mformetal.metallic.util.gone
import mformetal.metallic.util.safeObserver
import mformetal.metallic.util.visible
import javax.inject.Inject

/**
 * @author - mbpeele on 11/17/17.
 */
class OnboardingActivity : BaseActivity() {

    @Inject
    lateinit var factory : ViewModelProvider.Factory

    lateinit var viewModel : OnboardingViewModel

    @BindView(R.id.recycler) lateinit var recycler : RecyclerView
    private lateinit var adapter : ArtistNameAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app.component
                .onboarding(OnboardingActivityModule())
                .injectMembers(this)

        viewModel = ViewModelProviders.of(this, factory)[OnboardingViewModel::class.java]

        if (viewModel.hasUserOnboarded) {
            viewModel.onboard()
        } else {
            setContentView(R.layout.onboarding)
            ButterKnife.bind(this)

            recycler.layoutManager = LinearLayoutManager(this)
            recycler.setHasFixedSize(true)
            adapter = ArtistNameAdapter(viewModel.observeArtists())
            recycler.adapter = adapter

            viewModel.import()

            viewModel.observeImportFinishedEvent()
                    .observe(this, safeObserver {
                        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
                        val progressText = findViewById<TextView>(R.id.progress_text)

                        if (it) {
                            progressBar.gone()
                            progressText.gone()
                        } else {
                            progressBar.visible()
                            progressText.visible()
                        }
                    })
        }
    }

    @OnClick(R.id.done)
    fun onDoneButtonClicked() {
        if (viewModel.isImportFinished) {
            viewModel.onboard()
        } else {
            Snackbar.make(findViewById(R.id.coordinator),
                    getString(R.string.error_import_not_finished),
                    Snackbar.LENGTH_SHORT)
                    .show()
        }
    }
}