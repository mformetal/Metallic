package mformetal.metallic.util

import android.util.SparseArray

/**
 * Created by mbpeele on 11/19/17.
 */
class SelectionHandler<T> {

    private val items = SparseArray<T>()
    val selectedItems : List<T>
        get() {
            val list = mutableListOf<T>()
            for (i in 0 until items.size()) {
                items[i]?.let {
                    list.add(it)
                }
            }
            return list
        }

    fun select(position: Int, item: T) {
        items.put(position, item)
    }

    fun deselect(position: Int) {
        items.remove(position)
    }

    fun selected(position: Int) : Boolean = items[position] != null
}