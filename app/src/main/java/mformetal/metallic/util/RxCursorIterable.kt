package mformetal.metallic.util

import android.database.Cursor

/**
 * Created by peelemil on 12/7/17.
 */
class RxCursorIterable(private val mIterableCursor: Cursor) : Iterable<Cursor> {

    override fun iterator(): Iterator<Cursor> = RxCursorIterator.from(mIterableCursor)

    internal class RxCursorIterator(private val mCursor: Cursor) : Iterator<Cursor> {

        override fun hasNext(): Boolean = !mCursor.isClosed && mCursor.moveToNext()

        override fun next(): Cursor = mCursor

        companion object {

            fun from(cursor: Cursor): Iterator<Cursor> = RxCursorIterator(cursor)
        }
    }

    companion object {

        fun from(c: Cursor): RxCursorIterable = RxCursorIterable(c)
    }
}