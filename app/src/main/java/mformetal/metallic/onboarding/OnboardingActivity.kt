package mformetal.metallic.onboarding

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import mformetal.metallic.R
import mformetal.metallic.core.BaseActivity
import javax.inject.Inject

/**
 * Created by mbpeele on 11/17/17.
 */
class OnboardingActivity : BaseActivity() {

    @Inject
    lateinit var factory : ViewModelProvider.Factory

    lateinit var viewModel : OnboardingViewModel

    @BindView(R.id.recycler) lateinit var recycler : RecyclerView
    private lateinit var adapter : ArtistsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.onboarding)
        ButterKnife.bind(this)

        app.component
                .onboarding(OnboardingActivityModule())
                .injectMembers(this)

        viewModel = ViewModelProviders.of(this, factory)[OnboardingViewModel::class.java]

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.setHasFixedSize(true)
        adapter = ArtistsAdapter(viewModel.observeArtists())
        recycler.adapter = adapter

        viewModel.import()
    }

    @OnClick(R.id.done)
    fun onDoneButtonClicked() {
        viewModel.onArtistsSelected(adapter.selectedArtists)
    }
}