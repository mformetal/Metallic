package mformetal.metallic.util

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import android.view.View

/**
 * @author - mbpeele on 11/26/17.
 */
class GridItemDecoration(private val offset: Int) : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State) {
        outRect.top = offset
        outRect.bottom = offset
        outRect.left = offset
        outRect.right = offset
    }
}