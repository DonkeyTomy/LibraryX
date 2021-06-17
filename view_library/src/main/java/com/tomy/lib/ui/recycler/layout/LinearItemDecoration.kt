package com.tomy.lib.ui.recycler.layout

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**@author Tomy
 * Created by Tomy on 12/9/2020.
 */
class LinearItemDecoration: RecyclerView.ItemDecoration {

    private var mLeft   = 0
    private var mRight = 0
    private var mTop    = 0
    private var mBottom = 0

    constructor(left: Int, right: Int, top: Int, bottom: Int) {
        mLeft   = left
        mRight  = right
        mTop    = top
        mBottom = bottom
    }

    constructor(space: Int): this(space, space, space, space)


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val layoutManager: LinearLayoutManager = parent.layoutManager as LinearLayoutManager
        outRect.apply {
            if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
                left    = mLeft
                right   = mRight
                bottom  = mBottom * 2
                if (parent.getChildAdapterPosition(view) == 0) {
                    top = mTop * 2
                }
            } else {
                left  = mLeft * 2
                right = mRight * 2
                if (parent.getChildAdapterPosition(view) == layoutManager.itemCount - 1) {

                }
            }

        }
    }

}