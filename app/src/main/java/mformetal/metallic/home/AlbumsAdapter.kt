package mformetal.metallic.home

import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
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
import mformetal.metallic.data.Album
import mformetal.metallic.data.Artist
import mformetal.metallic.util.inflater

/**
 * Created by peelemil on 11/29/17.
 */
class AlbumsAdapter(results: RealmResults<Album>) : RealmRecyclerViewAdapter<Album, AlbumsAdapter.AlbumViewHolder>(results, true) {

    private var inflater : LayoutInflater?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        if (inflater == null) {
            inflater = parent.inflater
        }

        val view = inflater!!.inflate(R.layout.home_album_item, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = getItem(position)!!
        holder.bind(album)
    }

    inner class AlbumViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.album_name) lateinit var albumName: TextView
        @BindView(R.id.created_by) lateinit var albumArtist: TextView
        @BindView(R.id.album_image) lateinit var albumImage: ImageView

        init {
            ButterKnife.bind(this, itemView)
        }

        fun bind(album: Album) {
            album.artworkUrl?.let {
                GlideApp.with(itemView.context)
                        .load(it)
                        .fitCenter()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .override(400, 300)
                        .into(albumImage)
            }

            albumName.text = album.name
            albumArtist.text = album.createdBy
        }
    }
}