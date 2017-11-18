package mformetal.metallic.domain

/**
 * Created by mbpeele on 11/17/17.
 */
data class Artist(val name: String,
                  val albums: List<Album>) {
}

data class Album(val name: String, val songs: List<Song>) {
}

data class Song(val name: String) {
}