package mformetal.metallic.util

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * @author - mbpeele on 11/26/17.
 */
class GridItemDecoration(private val offset: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.top = offset
        outRect.bottom = offset
        outRect.left = offset
        outRect.right = offset
    }
}