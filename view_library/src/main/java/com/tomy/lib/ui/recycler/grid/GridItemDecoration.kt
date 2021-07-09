package com.tomy.lib.ui.recycler.grid

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**@author Tomy
 * Created by Tomy on 14/11/2020.
 */
/**
 *
 * @property space Int 间距
 * @property mIncludeEdge Boolean 左右边缘是否需要间距
 * @constructor
 */
class GridItemDecoration(val mLeft: Int, val mRight: Int, val mTop: Int, val mBottom: Int, val mIncludeEdge: Boolean = true): RecyclerView.ItemDecoration() {



    constructor(space: Int = 10, includeEdge: Boolean = true): this(space, space, space, space, includeEdge)

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
            outRect.left    = mLeft - column * mLeft / spanCount
            outRect.right   = (column + 1) * mRight / spanCount

        } else {
            outRect.left    = column * mLeft / spanCount
            outRect.right   = mRight - (column + 1) * mRight / spanCount
        }
        /**
         * 第一行的顶部需要加上完整的间距,除开第一行的顶部只需要间距的1/2,加上底部的1/2就是完整的间距.
         */
        if (position < spanCount) {
            outRect.top = mTop
        } else {
            outRect.top = mTop / 2
        }
        outRect.bottom  = mBottom / 2
    }

}