package mformetal.metallic.watchlist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import mformetal.metallic.R
import mformetal.metallic.core.GlideApp
import mformetal.metallic.data.NewArtist
import mformetal.metallic.util.inflater
import android.support.v7.app.AlertDialog

/**
 * Created by peelemil on 12/4/17.
 */
class WatchListAdapter(results : RealmResults<NewArtist>)
    : RealmRecyclerViewAdapter<NewArtist, WatchListAdapter.WatchlistViewHolder>(results, true) {

    private var inflater : LayoutInflater?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchListAdapter.WatchlistViewHolder {
        if (inflater == null) {
            inflater = parent.inflater
        }

        val view = inflater!!.inflate(R.layout.watch_list_artist_item, parent, false)
        return WatchlistViewHolder(view)
    }

    override fun onBindViewHolder(holder: WatchlistViewHolder, position: Int) {
        val artist = getItem(position)!!
        holder.bind(artist)
    }

    inner class WatchlistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.artist_name) lateinit var artistName : TextView
        @BindView(R.id.artist_image) lateinit var artistImage : ImageView

        init {
            ButterKnife.bind(this, itemView)

            itemView.setOnClickListener {
                val newArtist = getItem(adapterPosition)!!
                val albums = newArtist.albums!!
                val items = arrayOfNulls<CharSequence>(albums.size)
                for (i in albums.indices) {
                    items[i] = albums[i]!!.name
                }

                AlertDialog.Builder(it.context)
                        .setItems(items) { _, _ ->
                            // Handle click a little better
                        }
                        .setTitle(it.context.getString(R.string.watch_list_artist_dialog_title, newArtist.name))
                        .create()
                        .show()
            }
        }

        fun bind(newArtist: NewArtist) {
            val url = newArtist.artworkUrl ?: newArtist.albums?.firstOrNull { it.artworkUrl != null }?.artworkUrl
            url?.let {
                GlideApp.with(itemView.context)
                        .load(it)
                        .fitCenter()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(artistImage)
            }

            artistName.text = newArtist.name
        }
    }
}