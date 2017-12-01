package mformetal.metallic.data

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by mbpeele on 11/17/17.
 */
open class Artist(
        @PrimaryKey
        var name: String ?= null,
        var isWatching: Boolean = true,
        var spofityId: String ?= null,
        var artworkUrl: String ?= null,
        var albums: RealmList<Album> ?= null) : RealmObject()

open class Album(
        var name: String ?= null,
        var yearReleased: String ?= null,
        var createdBy: String ?= null,
        var spofityId: String ?= null,
        var artworkUrl: String ?= null,
        var songs: RealmList<Song> ?= null) : RealmObject()

open class Song(
        var name: String ?= null,
        var onAlbum: String ?= null,
        var createdBy: String ?= null,
        var spofityId: String ?= null) : RealmObject()