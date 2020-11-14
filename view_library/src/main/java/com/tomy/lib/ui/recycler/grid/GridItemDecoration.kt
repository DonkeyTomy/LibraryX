package com.tomy.lib.ui.recycler.grid

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 14/11/2020.
 */
class GridItemDecoration(val mSpace: Int, val mIncludeEdge: Boolean = true): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val manager = (parent.layoutManager as GridLayoutManager)
        val spanCount   = manager.spanCount
//        val lastPosition = manager.findLastVisibleItemPosition()
        val position    = parent.getChildLayoutPosition(view)
//        val adapterPosition = parent.getChildAdapterPosition(view)
//        Timber.d("position = $position; adapterPosition = $adapterPosition; last = $lastPosition")
        val column = position % spanCount
        if (mIncludeEdge) {
            outRect.left    = mSpace - column * mSpace / spanCount
            outRect.right   = (column + 1) * mSpace / spanCount
            if (position < spanCount) {
                outRect.top = mSpace * 2
            } else {
                outRect.top = mSpace
            }
            outRect.bottom  = mSpace
        } else {
            outRect.left    = column * mSpace / spanCount
            outRect.right   = mSpace - (column + 1) * mSpace / spanCount
            /*if (position >= spanCount) {
                outRect.top = mSpace
            }*/
            if (position < spanCount) {
                outRect.top = mSpace * 2
            } else {
                outRect.top = mSpace
            }
        }
    }

}