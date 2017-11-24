package mformetal.metallic.home

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import mformetal.metallic.R
import mformetal.metallic.data.Artist
import mformetal.metallic.util.inflater

/**
 * @author - mbpeele on 11/23/17.
 */
class ArtistsAdapter(artists: RealmResults<Artist>)
    : RealmRecyclerViewAdapter<Artist, ArtistsAdapter.ArtistViewHolder>(artists, true) {

    private var inflater : LayoutInflater?= null

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        val artist = getItem(position)!!
        holder.bind(artist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        if (inflater == null) {
            inflater = parent.inflater
        }
        val view = inflater!!.inflate(R.layout.artist_item, parent, false)
        return ArtistViewHolder(view)
    }

    inner class ArtistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.artist_name) lateinit var artistName : TextView
        @BindView(R.id.artist_image) lateinit var artistImage : ImageView

        init {
            ButterKnife.bind(this, itemView)
        }

        fun bind(artist: Artist) {
            artistName.text = artist.name
        }
    }
}