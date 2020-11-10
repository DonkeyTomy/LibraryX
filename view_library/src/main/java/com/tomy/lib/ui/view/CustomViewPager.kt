package com.tomy.lib.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**@author Tomy
 * Created by Tomy on 2018/11/9.
 */
class CustomViewPager(context: Context, attrs: AttributeSet): ViewPager(context, attrs) {

    private var mAllowScroll = true

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return mAllowScroll && super.onInterceptTouchEvent(ev)
    }

    fun setAllowScroll(allow: Boolean) {
        mAllowScroll = allow
    }
}