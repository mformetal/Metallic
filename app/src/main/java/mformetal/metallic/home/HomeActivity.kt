package mformetal.metallic.home

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.RecyclerView
import butterknife.BindView
import mformetal.metallic.R
import mformetal.metallic.core.BaseActivity
import javax.inject.Inject

/**
 * @author - mbpeele on 11/23/17.
 */
class HomeActivity : BaseActivity() {

    @Inject
    lateinit var factory : ViewModelProvider.Factory

    lateinit var viewModel : HomeViewModel

    @BindView(R.id.container) lateinit var container : CoordinatorLayout
    @BindView(R.id.recycler) lateinit var recycler : RecyclerView

    companion object {
        fun create(context: Context) = Intent(context, HomeActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app.component
                .home(HomeModule())
                .injectMembers(this)

        viewModel = ViewModelProviders.of(this, factory)[HomeViewModel::class.java]
    }
}