package com.tomy.lib.ui.recycler.layout

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**@author Tomy
 * Created by Tomy on 12/9/2020.
 */
class LinearItemDecoration(var space: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.apply {
            left    = space
            right   = space
            bottom  = space * 2
            if (parent.getChildAdapterPosition(view) == 0) {
                top = space * 2
            }
        }
    }

}