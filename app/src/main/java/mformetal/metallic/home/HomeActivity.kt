package mformetal.metallic.home

import android.app.SearchManager
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.Menu
import butterknife.BindView
import butterknife.ButterKnife
import mformetal.metallic.R
import mformetal.metallic.core.BaseActivity
import mformetal.metallic.util.GridItemDecoration
import javax.inject.Inject


/**
 * @author - mbpeele on 11/23/17.
 */
class HomeActivity : BaseActivity() {

    @Inject
    lateinit var factory : ViewModelProvider.Factory

    lateinit var viewModel : HomeViewModel

    @BindView(R.id.recycler) lateinit var recycler : RecyclerView
    @BindView(R.id.toolbar) lateinit var toolbar : Toolbar
    lateinit var adapter : HomeAdapter

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

        adapter = HomeAdapter(viewModel, viewModel.artists)

        recycler.addItemDecoration(GridItemDecoration(resources.getDimensionPixelOffset(R.dimen.spacing_normal)))
        recycler.layoutManager = GridLayoutManager(this, 2)
        recycler.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        // Retrieve the SearchView and plug it into SearchManager
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val results = viewModel.searchArtistsByName(query)
                adapter.updateData(results)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val results = viewModel.searchArtistsByName(newText)
                adapter.updateData(results)
                return true
            }
        })
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        return super.onCreateOptionsMenu(menu)
    }
}