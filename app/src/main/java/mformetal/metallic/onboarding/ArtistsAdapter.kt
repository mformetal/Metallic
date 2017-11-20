package mformetal.metallic.onboarding

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import mformetal.metallic.R
import mformetal.metallic.domain.Artist
import mformetal.metallic.util.SelectionHandler
import mformetal.metallic.util.inflater

/**
 * Created by mbpeele on 11/19/17.
 */
internal class ArtistsAdapter(private val artists: List<Artist>)
    : RecyclerView.Adapter<ArtistsAdapter.ArtistsViewHolder>() {

    private val selectionHandler: SelectionHandler<Artist> = SelectionHandler(artists.size)
    private var inflater : LayoutInflater ?= null

    val selectedArtists : List<Artist>
        get() = selectionHandler.selectedItems

    init {
        artists.forEachIndexed { index, artist ->
            selectionHandler.select(index, artist)
        }
    }

    override fun getItemCount(): Int = artists.count()

    override fun onBindViewHolder(holder: ArtistsViewHolder, position: Int) {
        val artist = artists[position]
        holder.bind(artist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistsViewHolder {
        if (inflater == null) {
            inflater = parent.inflater
        }
        val view = inflater!!.inflate(R.layout.checkable_artist_item, parent, false)
        return ArtistsViewHolder(view)
    }

    inner class ArtistsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.artist_name) lateinit var artistName : TextView
        @BindView(R.id.artist_checkbox) lateinit var checkBox : CheckBox

        init {
            ButterKnife.bind(this, itemView)

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectionHandler.select(adapterPosition, artists[adapterPosition])
                } else {
                    selectionHandler.deselect(adapterPosition)
                }
            }
        }

        fun bind(artist: Artist) {
            checkBox.isChecked = selectionHandler.selected(adapterPosition)

            artistName.text = artist.name
        }
    }
}