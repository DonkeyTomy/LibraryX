package com.tomy.lib.ui.recycler

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import com.yanzhenjie.recyclerview.SwipeRecyclerView

/**@author Tomy
 * Created by Tomy on 31/12/2020.
 */
class CustomRefreshRecycler: SwipeRecyclerView {
    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int): super(context, attributeSet, defStyle) {
        isChildrenDrawingOrderEnabled = true
    }
    constructor(context: Context, attributeSet: AttributeSet?): this(context, attributeSet, 0)
    constructor(context: Context): this(context, null)

    /*override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val result = super.dispatchKeyEvent(event)
        val focusView = focusedChild
        Timber.d("dispatchKeyEvent(). eventCode = ${event.keyCode}; focusedChild = $focusView")
        if (focusView == null) {
            val holder = findViewHolderForAdapterPosition(0)
            requestFocusFromTouch()
            holder?.itemView!!.requestFocus()
            requestChildFocus(holder.itemView, holder.itemView)
        } else {
            if (event.action == KeyEvent.ACTION_UP) {
                return true
            }
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_UP    -> {
                    val upView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_UP)
                    Timber.d("upView = $upView")
                    return if (upView != null) {
                        upView.requestFocus()
                        val upOffset = height / 2 - (upView.bottom - upView.height / 2)
                        smoothScrollBy(0, -upOffset)
                        true
                    } else {
                        result
                    }
                }
                KeyEvent.KEYCODE_DPAD_DOWN  -> {
                    val downView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_DOWN)
                    Timber.d("downView = $downView")
                    return if (downView != null) {
                        downView.requestFocus()
                        val downOffset = downView.top + downView.height / 2 - height / 2
                        smoothScrollBy(0, downOffset)
                        true
                    } else {
                        true
                    }
                }
            }
        }
        return result
    }*/

    private fun isVisitBottom(): Boolean {
        val layoutManager = this.layoutManager
        if (layoutManager is LinearLayoutManager) {
            val lastVisibleItem     = layoutManager.findLastVisibleItemPosition()
            val visibleItemCount    = layoutManager.childCount
            val totalItemCount      = layoutManager.itemCount
            if (visibleItemCount > 0 && lastVisibleItem == totalItemCount - 1) {
                return true
            }
        }
        return false
    }

    fun isVertical(): Boolean {
        val layoutManager = this.layoutManager
        if (layoutManager is LinearLayoutManager) {
            return layoutManager.orientation == LinearLayoutManager.VERTICAL
        }
        return false
    }

    fun isTopEdge(childPosition: Int): Boolean {
        if (layoutManager is LinearLayoutManager) {
            if (isVertical()) {
                return childPosition == 0
            }
        }
        return false
    }
    /**
     * 是否是最下边的Item
     * @param childPosition Int
     * @return Boolean
     */
    fun isBottomEdge(childPosition: Int): Boolean {
        if (layoutManager is LinearLayoutManager) {
            if (isVertical()) {
                return childPosition == (layoutManager as LinearLayoutManager).itemCount - 1
            }
        }
        return false
    }

}