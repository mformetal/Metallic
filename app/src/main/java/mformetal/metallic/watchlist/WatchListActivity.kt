package mformetal.metallic.watchlist

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import mformetal.metallic.R
import mformetal.metallic.core.BaseActivity
import mformetal.metallic.home.ArtistsAdapter
import mformetal.metallic.util.GridItemDecoration
import javax.inject.Inject

/**
 * Created by peelemil on 12/4/17.
 */
class WatchListActivity : BaseActivity() {

    @BindView(R.id.recycler) lateinit var recycler : RecyclerView
    @BindView(R.id.toolbar) lateinit var toolbar : Toolbar

    @Inject
    lateinit var factory : ViewModelProvider.Factory
    lateinit var viewModel : WatchListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.watch_list)
        ButterKnife.bind(this)

        app.component
                .watchList(WatchListModule())
                .injectMembers(this)

        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.watch_list_toolbar_title)

        viewModel = ViewModelProviders.of(this, factory)[WatchListViewModel::class.java]

        recycler.addItemDecoration(GridItemDecoration(resources.getDimensionPixelOffset(R.dimen.spacing_normal)))
        recycler.layoutManager = GridLayoutManager(this, 2)
        recycler.adapter =  WatchListAdapter(viewModel.newArtists)
    }
}