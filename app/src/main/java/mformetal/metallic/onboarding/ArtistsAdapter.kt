package mformetal.metallic.onboarding

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import mformetal.metallic.R
import mformetal.metallic.domain.Artist
import mformetal.metallic.util.inflater

/**
 * Created by mbpeele on 11/19/17.
 */
class ArtistsAdapter(private val artists: MutableList<Artist>) : RecyclerView.Adapter<ArtistsAdapter.ArtistsViewHolder>() {

    override fun getItemCount(): Int = artists.count()

    override fun onBindViewHolder(holder: ArtistsViewHolder, position: Int) {
        val artist = artists[position]
        holder.bind(artist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistsViewHolder {
        val view = parent.inflater.inflate(R.layout.artist_item, parent, false)
        return ArtistsViewHolder(view)
    }

    fun add(artist: Artist) {
        artists.add(artist)
        notifyItemInserted(artists.size)
    }

    class ArtistsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val artistName : TextView = itemView.findViewById(R.id.artist_name)

        fun bind(artist: Artist) {
            artistName.text = artist.name
        }
    }
}