package com.tomy.lib.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**@author Tomy
 * Created by Tomy on 15/9/2020.
 */
class DefaultBehavior(context: Context, attrs: AttributeSet): FloatingActionButton.Behavior(context, attrs) {

    @Volatile
    private var mOutAnimating = false

    @Volatile
    private var mInAnimating = false

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: FloatingActionButton,
                                     directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type)
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: FloatingActionButton, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int, consumed: IntArray) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed)
        if (dyConsumed != 0) {
            animateOut(child)
        } else {
            animateIn(child)
        }
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: FloatingActionButton, target: View, type: Int) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type)
        animateIn(child)
    }

    //隐藏
    fun animateOut(fab: FloatingActionButton) {
        if (!mOutAnimating) {
            mInAnimating = false
            mOutAnimating = true
            val layoutParams = fab.layoutParams as CoordinatorLayout.LayoutParams
            fab.animate().translationY((fab.height + layoutParams.bottomMargin).toFloat()).setInterpolator(LinearInterpolator()).start()
        }
    }

    //显示
    fun animateIn(fab: FloatingActionButton) {
        if (!mInAnimating) {
            mInAnimating = true
            mOutAnimating = false
            fab.animate().translationY(0f).setInterpolator(LinearInterpolator()).start()
        }
    }

}