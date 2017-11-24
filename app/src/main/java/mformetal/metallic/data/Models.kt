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
        var spofityId: String ?= null,
        var artworkUrl: String ?= null,
        var albums: RealmList<Album> ?= null) : RealmObject()

open class Album(
        @PrimaryKey
        var name: String ?= null,
        var spofityId: String ?= null,
        var artworkUrl: String ?= null,
        var songs: RealmList<Song> ?= null) : RealmObject()

open class Song(
        @PrimaryKey
        var name: String ?= null,
        var spofityId: String ?= null) : RealmObject()