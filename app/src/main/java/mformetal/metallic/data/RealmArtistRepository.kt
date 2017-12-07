package mformetal.metallic.data

import io.reactivex.Completable
import io.realm.Realm

/**
 * @author - mbpeele on 12/6/17.
 */
class RealmArtistRepository : ArtistRepository {

    override fun saveArtist(artist: Artist): Completable {
        return Completable.create { emitter ->
            val realm = Realm.getDefaultInstance()
            realm.executeTransactionAsync({
                it.insertOrUpdate(artist)
            }, {
                emitter.onComplete()
                realm.close()
            }, {
                emitter.onError(it)
                realm.close()
            })
        }
    }
}