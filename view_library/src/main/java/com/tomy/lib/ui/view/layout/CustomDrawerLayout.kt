package com.tomy.lib.ui.view.layout

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GravityCompat
import androidx.core.view.MotionEventCompat
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout


/**@author Tomy
 * Created by Tomy on 21/4/2021.
 */
class CustomDrawerLayout(context: Context, attributeSet: AttributeSet?, defStyle: Int) : DrawerLayout(
    context,
    attributeSet,
    defStyle
) {

    private var mInitialMotionX = 0f
    private var mInitialMotionY = 0f
    private var mRightBelowView: View? = null
    private var mRightAboveView: View? = null
    private val mTouchSlop = 0

    constructor(context: Context): this(context, null, 0)
    constructor(context: Context, attributeSet: AttributeSet?): this(context, attributeSet, 0)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val childCount = childCount
        // 双层Drawer, 原生会抛出异常
        // 双层Drawer, 原生会抛出异常
        try {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        } catch (e: IllegalStateException) {
            /** 默认最后一个子View是第二层Drawer  */
            // 因为抛出异常, 所以手动测量第二层Drawer
            val density = resources.displayMetrics.density
            val minDrawerMargin = (64 * density + 0.5f).toInt()
            val child = getChildAt(childCount - 1)
            val lp = child.layoutParams as LayoutParams
            val drawerWidthSpec = getChildMeasureSpec(
                widthMeasureSpec,
                minDrawerMargin + lp.leftMargin + lp.rightMargin,
                lp.width
            )
            val drawerHeightSpec = getChildMeasureSpec(
                heightMeasureSpec,
                lp.topMargin + lp.bottomMargin,
                lp.height
            )
            child.measure(drawerWidthSpec, drawerHeightSpec)
        }

        // 找出两个Drawer, 用来处理点击收起Drawer问题

        // 找出两个Drawer, 用来处理点击收起Drawer问题
        if (mRightBelowView == null || mRightAboveView == null) {
            mRightBelowView = null
            mRightAboveView = null
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (checkDrawerViewAbsoluteGravity(child, Gravity.RIGHT)) {
                    if (mRightBelowView == null) {
                        mRightBelowView = child
                    } else {
                        mRightAboveView = child
                    }
                }
            }
        }
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        /** 双层Drawer时, 不能正常通过点击收起第一层Drawer, 所以在这里自己处理  */
        val action = ev.action
        when (action and MotionEventCompat.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                val x = ev.x
                val y = ev.y
                mInitialMotionX = x
                mInitialMotionY = y
            }
            MotionEvent.ACTION_UP -> {
                if (mRightAboveView != null && mRightBelowView != null) {
                    val x = ev.x
                    val y = ev.y
                    if (x < mRightBelowView!!.left) {
                        val dx = x - mInitialMotionX
                        val dy = y - mInitialMotionY
                        val slop = mTouchSlop
                        if (dx * dx + dy * dy < slop * slop) {
                            // 当第二层Drawer没有打开而第一层Drawer打开时, 收起第一层Drawer
                            if (!isDrawerOpen(mRightAboveView!!) && isDrawerOpen(mRightBelowView!!)) {
                                closeDrawer(mRightBelowView!!)
                                return true
                            }
                        }
                    }
                }
            }
        }
        // 其他情况使用默认代码
        return super.onTouchEvent(ev)
    }

    fun getDrawerViewAbsoluteGravity(drawerView: View): Int {
        val gravity = (drawerView.layoutParams as LayoutParams).gravity
        return GravityCompat.getAbsoluteGravity(gravity, ViewCompat.getLayoutDirection(this))
    }

    fun checkDrawerViewAbsoluteGravity(drawerView: View, checkFor: Int): Boolean {
        val absGravity = getDrawerViewAbsoluteGravity(drawerView)
        return absGravity and checkFor == checkFor
    }
}