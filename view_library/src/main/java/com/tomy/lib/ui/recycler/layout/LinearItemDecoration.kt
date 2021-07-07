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

    private var isSameSpace = false

    constructor(left: Int, right: Int, top: Int, bottom: Int, sameSpace: Boolean = false) {
        mLeft   = left
        mRight  = right
        mTop    = top
        mBottom = bottom
        isSameSpace = sameSpace
    }

    constructor(space: Int, sameSpace: Boolean = false): this(space, space, space, space, sameSpace)


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val layoutManager: LinearLayoutManager = parent.layoutManager as LinearLayoutManager
        outRect.apply {
            if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
                left    = if (isSameSpace) mLeft * 2 else mLeft
                right   = if (isSameSpace) mRight * 2 else mRight
                top = if (parent.getChildAdapterPosition(view) == 0) {
                    mTop * 2
                } else {
                    mTop
                }
                bottom  = if (parent.getChildAdapterPosition(view) == layoutManager.itemCount - 1) {
                    mBottom * 2
                } else {
                    mBottom
                }
            } else {
                top    = if (isSameSpace) mTop * 2 else mTop
                bottom   = if (isSameSpace) mBottom * 2 else mBottom
                left = if (parent.getChildAdapterPosition(view) == 0) {
                    mLeft * 2
                } else {
                    mLeft
                }
                right  = if (parent.getChildAdapterPosition(view) == layoutManager.itemCount - 1) {
                    mRight * 2
                } else {
                    mRight
                }
            }

        }
    }

}