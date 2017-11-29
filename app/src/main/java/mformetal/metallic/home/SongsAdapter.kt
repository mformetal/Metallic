package mformetal.metallic.home

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import mformetal.metallic.R
import mformetal.metallic.data.Album
import mformetal.metallic.data.Song
import mformetal.metallic.util.inflater

/**
 * Created by peelemil on 11/29/17.
 */
class SongsAdapter(results: RealmResults<Song>) : RealmRecyclerViewAdapter<Song, SongsAdapter.SongViewHolder>(results, true) {

    private var inflater : LayoutInflater ?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        if (inflater == null) {
            inflater = parent.inflater
        }

        val view = inflater!!.inflate(R.layout.home_song_item, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = getItem(position)!!
        holder.bind(song)
    }

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.song_name) lateinit var songName: TextView
        @BindView(R.id.created_by) lateinit var songArtist: TextView
        @BindView(R.id.on_album) lateinit var songAlbum: TextView

        init {
            ButterKnife.bind(this, itemView)
        }

        fun bind(song: Song) {
            songName.text = song.name
            songArtist.text = song.createdBy
            songAlbum.text = song.onAlbum
        }
    }
}