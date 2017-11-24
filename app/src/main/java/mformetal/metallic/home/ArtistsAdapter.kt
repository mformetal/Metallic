package mformetal.metallic.home

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import mformetal.metallic.R
import mformetal.metallic.core.GlideApp
import mformetal.metallic.data.Artist
import mformetal.metallic.util.inflater

/**
 * @author - mbpeele on 11/23/17.
 */
class ArtistsAdapter(private val delegate: ArtistsAdapterClickDelegate, artists: RealmResults<Artist>)
    : RealmRecyclerViewAdapter<Artist, ArtistsAdapter.ArtistViewHolder>(artists, true) {

    private var inflater : LayoutInflater?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        if (inflater == null) {
            inflater = parent.inflater
        }
        val view = inflater!!.inflate(R.layout.home_artist_item, parent, false)
        return ArtistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        val artist = getItem(position)!!
        holder.bind(artist)
    }

    inner class ArtistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.artist_name) lateinit var artistName : TextView
        @BindView(R.id.artist_image) lateinit var artistImage : ImageView
        @BindView(R.id.artist_options) lateinit var artistOptions : ImageButton

        init {
            ButterKnife.bind(this, itemView)

            artistOptions.setOnClickListener {
                val popup = PopupMenu(it.context, it)
                popup.menuInflater.inflate(R.menu.home_artist_item_menu, popup.menu)
                popup.setOnMenuItemClickListener {
                    val artist = getItem(adapterPosition)!!
                    when (it.itemId) {
                        R.id.add_to_watchlist -> {
                            delegate.onArtistClickedForWatchList(artist)
                        }
                    }
                    true
                }
                popup.show()
            }
        }

        fun bind(artist: Artist) {
            val url = artist.artworkUrl ?: artist.albums?.firstOrNull { it.artworkUrl != null }?.artworkUrl
            url?.let {
                GlideApp.with(itemView.context)
                        .load(it)
                        .transition(withCrossFade())
                        .into(artistImage)
            }

            artistName.text = artist.name
        }
    }
}