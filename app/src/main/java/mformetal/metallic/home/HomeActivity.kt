package mformetal.metallic.home

import android.app.SearchManager
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.widget.*
import android.view.Menu
import android.view.ViewParent
import butterknife.BindView
import butterknife.ButterKnife
import mformetal.metallic.R
import mformetal.metallic.core.BaseActivity
import mformetal.metallic.data.Artist
import mformetal.metallic.util.GridItemDecoration
import mformetal.metallic.util.VerticalItemDecoration
import javax.inject.Inject


/**
 * @author - mbpeele on 11/23/17.
 */
class HomeActivity : BaseActivity() {

    @Inject
    lateinit var factory : ViewModelProvider.Factory

    lateinit var viewModel : HomeViewModel

    @BindView(R.id.toolbar) lateinit var toolbar : Toolbar
    @BindView(R.id.tabs) lateinit var tabLayout : TabLayout
    @BindView(R.id.recycler) lateinit var recycler : RecyclerView

    companion object {
        fun create(context: Context) : Intent = Intent(context, HomeActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.home)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)

        app.component
                .home(HomeModule())
                .injectMembers(this)

        viewModel = ViewModelProviders.of(this, factory)[HomeViewModel::class.java]

        recycler.addItemDecoration(GridItemDecoration(resources.getDimensionPixelOffset(R.dimen.spacing_normal)))
        recycler.layoutManager = GridLayoutManager(this, 2)
        recycler.adapter =  ArtistsAdapter(clickDelegate, viewModel.artists)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) { }

            override fun onTabUnselected(tab: TabLayout.Tab?) { }

            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.text) {
                    getString(R.string.tab_artists) -> {
                        recycler.removeItemDecorationAt(0)
                        recycler.addItemDecoration(GridItemDecoration(resources.getDimensionPixelOffset(R.dimen.spacing_normal)))

                        recycler.layoutManager = GridLayoutManager(this@HomeActivity, 2)
                        recycler.adapter = ArtistsAdapter(clickDelegate, viewModel.artists)
                    }
                    getString(R.string.tab_albums) -> {
                        recycler.removeItemDecorationAt(0)
                        recycler.addItemDecoration(GridItemDecoration(resources.getDimensionPixelOffset(R.dimen.spacing_normal)))

                        recycler.layoutManager = GridLayoutManager(this@HomeActivity, 2)
                        recycler.adapter = AlbumsAdapter(viewModel.albums)
                    }
                    getString(R.string.tab_songs) -> {
                        recycler.removeItemDecorationAt(0)
                        recycler.addItemDecoration(VerticalItemDecoration(resources.getDimensionPixelOffset(R.dimen.spacing_normal)))

                        recycler.layoutManager = LinearLayoutManager(this@HomeActivity)
                        recycler.adapter = SongsAdapter(viewModel.songs)
                    }
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        // Retrieve the SearchView and plug it into SearchManager
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
//                val results = viewModel.searchArtistsByName(query)
//                adapter.updateData(results)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
//                val results = viewModel.searchArtistsByName(newText)
//                adapter.updateData(results)
                return true
            }
        })
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        return super.onCreateOptionsMenu(menu)
    }

    private val clickDelegate = object : HomeAdapterClickDelegate {
        override fun addArtistToWatchList(artist: Artist) {
            viewModel.addArtistToWatchList(artist)
        }

        override fun removeArtistFromWatchList(artist: Artist) {
            viewModel.removeArtistFromWatchList(artist)
        }
    }
}