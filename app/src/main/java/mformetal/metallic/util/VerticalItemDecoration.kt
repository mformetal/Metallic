package mformetal.metallic.util

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import android.view.View

/**
 * Created by peelemil on 11/29/17.
 */
class VerticalItemDecoration(private val offset: Int) : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State) {
        outRect.left = offset
        outRect.right = offset
        outRect.bottom = offset

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = offset
        }
    }
}