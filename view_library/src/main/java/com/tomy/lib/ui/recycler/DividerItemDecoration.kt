package com.tomy.lib.ui.recycler

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**@author Tomy
 * Created by Tomy on 2018/7/31.
 */
class DividerItemDecoration: RecyclerView.ItemDecoration() {

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
    }

    companion object {
        const val HORIZONTAL_LIST   = LinearLayoutManager.HORIZONTAL
        const val VERTICAL_LIST     = LinearLayoutManager.VERTICAL
    }
}