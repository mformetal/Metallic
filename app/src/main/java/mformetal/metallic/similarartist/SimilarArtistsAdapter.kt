package mformetal.metallic.similarartist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import mformetal.metallic.R
import mformetal.metallic.core.GlideApp
import mformetal.metallic.data.Artist
import mformetal.metallic.util.inflater

/**
 * @author - mbpeele on 11/26/17.
 */
class SimilarArtistsAdapter(private val artists: List<Artist>,
                            private val delegate: SimilarArtistsAdapterClickDelegate)
    : RecyclerView.Adapter<SimilarArtistsAdapter.SimilarArtistsViewHolder>() {

    private var inflater : LayoutInflater ?= null

    override fun getItemCount(): Int = artists.size

    private fun getItem(position: Int) = artists[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarArtistsViewHolder {
        if (inflater == null) {
            inflater = parent.inflater
        }
        val view = inflater!!.inflate(R.layout.artist_detail_similar_item, parent, false)
        return SimilarArtistsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SimilarArtistsViewHolder, position: Int) {
        val artist = getItem(position)
        holder.bind(artist)
    }

    inner class SimilarArtistsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.artist_name) lateinit var artistName: TextView
        @BindView(R.id.artist_image) lateinit var artistImage: ImageView

        init {
            ButterKnife.bind(this, itemView)

            itemView.setOnClickListener {
                delegate.onArtistClicked(getItem(adapterPosition))
            }
        }

        fun bind(artist: Artist) {
            GlideApp.with(itemView.context)
                    .load(artist.artworkUrl!!)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(artistImage)

            artistName.text = artist.name
        }
    }
}