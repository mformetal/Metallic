package mformetal.metallic.onboarding

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import mformetal.metallic.R
import mformetal.metallic.data.Artist
import mformetal.metallic.util.SelectionHandler
import mformetal.metallic.util.inflater

/**
 * Created by mbpeele on 11/19/17.
 */
internal class ArtistsAdapter(artists: RealmResults<Artist>)
    : RealmRecyclerViewAdapter<Artist, ArtistsAdapter.ArtistsViewHolder>(artists, true) {

    private val selectionHandler: SelectionHandler<Artist> = SelectionHandler(artists.size)
    private var inflater : LayoutInflater ?= null

    val selectedArtists : List<Artist>
        get() = selectionHandler.selectedItems

    init {
        artists.forEachIndexed { index, artist ->
            selectionHandler.select(index, artist)
        }
    }

    override fun onBindViewHolder(holder: ArtistsViewHolder, position: Int) {
        val artist = getItem(position)!!
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
                val artist = getItem(adapterPosition)!!
                if (isChecked) {
                    selectionHandler.select(adapterPosition, artist)
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